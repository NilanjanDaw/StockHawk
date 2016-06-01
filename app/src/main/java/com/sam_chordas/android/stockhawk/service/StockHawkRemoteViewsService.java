package com.sam_chordas.android.stockhawk.service;

import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

public class StockHawkRemoteViewsService extends RemoteViewsService {
    public StockHawkRemoteViewsService() {
    }


    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     *
     * @param intent
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            Cursor data = null;
            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                    data = null;
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        QuoteProvider.Quotes.CONTENT_URI,
                        null,
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        QuoteColumns.CREATED + " ASC"
                );
                Binder.restoreCallingIdentity(identityToken);

            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return (data == null) ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position))
                    return null;
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                remoteViews.setTextViewText(R.id.stock_symbol, data.getString(data.getColumnIndex(QuoteColumns.SYMBOL)));
                remoteViews.setTextViewText(R.id.bid_price, data.getString(data.getColumnIndex(QuoteColumns.BIDPRICE)));
                remoteViews.setTextViewText(R.id.change, data.getString(data.getColumnIndex(QuoteColumns.CHANGE)));
                if (data.getInt(data.getColumnIndex("is_up")) == 1) {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getInt(data.getColumnIndex(QuoteColumns._ID));
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }

}
