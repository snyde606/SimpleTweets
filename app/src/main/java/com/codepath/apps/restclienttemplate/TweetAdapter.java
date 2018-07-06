package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.net.ParseException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    private Context context;
    private ClickListener listen;

    public TweetAdapter(List<Tweet> tweets, ClickListener lis){
        mTweets = tweets;
        listen = lis;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView, listen);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = mTweets.get(position);
        String timeAgo = getRelativeTimeAgo(tweet.createdAt);

        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvTimeAgo.setText(timeAgo);
        SpannableString content = new SpannableString("@" + tweet.user.getScreenName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.tvTag.setText(content);
        Glide.with(context).load(tweet.user.profileImageUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder).fitCenter())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(25,0, RoundedCornersTransformation.CornerType.ALL)))
                        .into(holder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvTimeAgo;
        public ImageView ivReply;
        public TextView tvTag;
        private WeakReference<ClickListener> listenerRef;

        public ViewHolder(View itemView, ClickListener listener){
            super(itemView);

            listenerRef = new WeakReference<>(listener);
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvTimeAgo = (TextView) itemView.findViewById(R.id.tvTimeAgo);
            ivReply = (ImageView) itemView.findViewById(R.id.ivReply);
            tvTag = (TextView) itemView.findViewById(R.id.tvTag);

            ivReply.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == ivReply.getId())
                listenerRef.get().onPositionClicked(getAdapterPosition());
        }
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }

    public interface ClickListener {

        void onPositionClicked(int position);

    }

}
