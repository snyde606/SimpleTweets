package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity{

    Button btCompose;
    EditText etCompose;
    TextView tvCharRemain;
    TwitterClient client;
    String username;
    boolean replying;
    long replyId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        username = getIntent().getStringExtra("reply");
        replying = !username.equals("0000");

        replyId = (getIntent().getLongExtra("id", 0));

        btCompose = (Button) findViewById(R.id.btCompose);
        etCompose = (EditText) findViewById(R.id.etCompose);
        tvCharRemain = (TextView) findViewById(R.id.tvCharRemain);
        client = TwitterApp.getRestClient(this);

        if(replying) {
            btCompose.setText("Reply");
            etCompose.setText("@" + username + " ");
            etCompose.setSelection(etCompose.getText().length());
        }

        int remain = 140 - etCompose.length();
        tvCharRemain.setText("Characters remaining: " + remain);
        tvCharRemain.setTextColor(Color.BLACK);

        setupButtonListener();

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int remain = 140 - etCompose.length();
                tvCharRemain.setText("Characters remaining: " + remain);
                if(remain < 0)
                    tvCharRemain.setTextColor(Color.RED);
                else
                    tvCharRemain.setTextColor(Color.BLACK);
            }
        });

    }

    private void setupButtonListener() {
        btCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetText = etCompose.getText().toString();
                client.sendTweet(tweetText, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {

                            Tweet tweet = Tweet.fromJSON(response);

                            Intent i = new Intent();
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);

                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, replying, replyId);
            }
        });
    }

}
