package com.example.justin.verbeterjegemeente.Presentation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.justin.verbeterjegemeente.Database.DatabaseHanlder;
import com.example.justin.verbeterjegemeente.R;

import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;

import com.example.justin.verbeterjegemeente.domain.Melding;
import com.squareup.picasso.Picasso;


/**
 * Created by Justin on 19-5-2017.
 */

public class DetailedMeldingActivity extends FragmentActivity {
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    private LikeButton likeButton;
    private Button terugButton;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private Melding melding;
    private TextView statusDetailed, statusDetailedNote, laatstUpdateDetailed, beschrijvingDetailed, hoofdCategorieDetailed, subCategorieDetailed;
    private ImageButton imageSmall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_melding);

       Bundle extras = getIntent().getExtras();

       final String origin  = extras.getString("ORIGIN");

       final ServiceRequest serviceRequest = (ServiceRequest)getIntent().getSerializableExtra("serviceRequest");


        likeButton = (LikeButton) findViewById(R.id.favorietenknopdetail);
        statusDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_status_DetailedID);
        statusDetailedNote = (TextView) findViewById(R.id.activityDetailedMelding_tv_status_DetailedNotesID);
        laatstUpdateDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_laatsUpdate_detailedID);
        beschrijvingDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_beschrijving_DetailedID);
        hoofdCategorieDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_hoofdCategorie_detailedID);
        subCategorieDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_subCategorie_detailedID);
        imageSmall = (ImageButton) findViewById(R.id.activityDetailedMelding_imgbtn_imageSmall_ID);

        statusDetailed.setText(serviceRequest.getStatus());
        statusDetailedNote.setText(serviceRequest.getStatusNotes());
        laatstUpdateDetailed.setText(serviceRequest.getUpdatedDatetime());
        beschrijvingDetailed.setText(serviceRequest.getDescription());
        hoofdCategorieDetailed.setText(serviceRequest.getServiceCode());
        subCategorieDetailed.setText(serviceRequest.getServiceCode());

        Picasso.with(getApplicationContext()).load(serviceRequest.getMediaUrl()).into(imageSmall);




        final DatabaseHanlder db = new DatabaseHanlder(getApplicationContext(), null, null, 1 );

        if(db.ReportExists(serviceRequest.getServiceRequestId())){
            likeButton.setLiked(true);
        }else{
            likeButton.setLiked(false);
        }

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                try {
                    if (db.ReportExists(serviceRequest.getServiceRequestId()) == false) {
                        db.addReport(serviceRequest.getServiceRequestId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                try {
                    if (db.ReportExists(serviceRequest.getServiceRequestId())) {
                        db.deleteReport(serviceRequest.getServiceRequestId());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        terugButton = (Button) findViewById(R.id.activityDetailedMelding_btn_terugBTN_ID);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(origin.equals("FollowActivity")){
                    Intent in = new Intent(getApplicationContext(), FollowingActivity.class);
                    startActivity(in);
                }else if(origin.equals("Tab2Fragment")){
                    Intent in = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(in);
                }

            }
        });
















        // Hook up clicks on the thumbnail views.

//        final View thumb1View = findViewById(R.id.fotomelding);
//        thumb1View.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                zoomImageFromThumb(thumb1View, R.drawable.thumb1);
//            }
//        });

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }
    private void zoomImageFromThumb(final View thumbView, int imageResId) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);
        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
