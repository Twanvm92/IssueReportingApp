package com.example.justin.verbeterjegemeente.Presentation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.justin.verbeterjegemeente.API.RequestManager;
import com.example.justin.verbeterjegemeente.Database.DatabaseHandler;
import com.example.justin.verbeterjegemeente.R;
import com.example.justin.verbeterjegemeente.domain.ServiceRequest;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DetailedMeldingActivity extends FragmentActivity {
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    private String origin;
    private TextView numbOfUpvoted;
    private ImageButton imageSmall;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_melding);

        Bundle extras = getIntent().getExtras();

        origin = extras.getString("ORIGIN");

        final ServiceRequest serviceRequest = (ServiceRequest) getIntent().getSerializableExtra("serviceRequest");
        String serviceRequestID = serviceRequest.getServiceRequestId();

        LikeButton likeButton = (LikeButton) findViewById(R.id.favorietenknopdetail);
        ImageButton upvoteButton = (ImageButton) findViewById(R.id.upvoteknopdetail);
        TextView statusDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_status_DetailedID);
        TextView laatstUpdateDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_laatsUpdate_detailedID);
        TextView beschrijvingDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_beschrijving_DetailedID);
        TextView hoofdCategorieDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_hoofdCategorie_detailedID);
        TextView subCategorieDetailed = (TextView) findViewById(R.id.activityDetailedMelding_tv_subCategorie_detailedID);
        imageSmall = (ImageButton) findViewById(R.id.activityDetailedMelding_imgbtn_imageSmall_ID);
        TextView statusNotes = (TextView) findViewById(R.id.activityDetailedMelding_tv_status_DetailedNotesID);
        numbOfUpvoted = (TextView) findViewById(R.id.activityDetailedMelding_tv_numberOfUpvotes);

        statusDetailed.setText(serviceRequest.getStatus());
        laatstUpdateDetailed.setText(serviceRequest.getUpdatedDatetime());
        beschrijvingDetailed.setText(serviceRequest.getDescription());
        hoofdCategorieDetailed.setText(serviceRequest.getServiceCode());
        subCategorieDetailed.setText(serviceRequest.getServiceCode());
        statusNotes.setText(serviceRequest.getStatusNotes());
        numbOfUpvoted.setText(String.valueOf(serviceRequest.getUpvotes()));

        // get the lsit of media urls inside the service request
        List<String> srMediaUrls = serviceRequest.getMediaUrls();
        if (!srMediaUrls.isEmpty()) {
            String mediaURL = serviceRequest.getMediaUrls().get(0);
            // load image from service request into imageview
            Picasso.with(getApplicationContext()).load(mediaURL).fit().into(imageSmall);

            Log.i("DetailMeldingActivity: ", "Mediaurl: " + mediaURL);
        }






        // This if statement checks if the selected ServiceRequest is already in the database, if so it sets the like button
        // liked, if not, it sets the button to unLiked.
        DatabaseHandler db = new DatabaseHandler(getApplicationContext(), null, null, 1 );
        if(db.ReportExists(serviceRequestID)){

            likeButton.setLiked(true);
        } else {
            likeButton.setLiked(false);
        }

        likeButton.setOnLikeListener(new OnLikeListener() {
            @Override

            // if the like star is pressed when the button is not liked, the like method is called.
            // This method adds the selectedServiceRequest to the database and sets the star to liked.
            public void liked(LikeButton likeButton) {

                try {
                    if (!db.ReportExists(serviceRequestID)) {
                        db.addReport(serviceRequestID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // When the star is presses when the button is liked, the unLike method is called.
            // This method deletes the selected.ServiceRequest from the database and set the button to unLiked.
            @Override
            public void unLiked(LikeButton likeButton) {
                try {
                    if (db.ReportExists(serviceRequestID)) {
                        db.deleteReport(serviceRequestID);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        upvoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!db.upvoteExists(serviceRequestID)) {
                    // add upvote to sqlite database
                    db.addUpvote(serviceRequestID);
                    // add upvote to service request in Gemeente Database
                    sendUpvoteToAPI(serviceRequestID);
                    addUpvoteToTextview();
                    // let user know service request is upvoted
                    Toast.makeText(DetailedMeldingActivity.this, getString(R.string.upvoteSucces), Toast.LENGTH_SHORT).show();
                } else {
                    // let user know he/she already upvoted this service request
                    Toast.makeText(DetailedMeldingActivity.this, getString(R.string.alreadyUpvoted), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // This button takes the user back to the previous screen, based on the ORIGIN value.
        Button terugButton = (Button) findViewById(R.id.activityDetailedMelding_btn_terugBTN_ID);
        terugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    // TODO: 24-8-2017 Is this still going to be used?
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
                                        View.Y, startBounds.top))
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

    /**
     * Send a post request to Open311 Interface notifying about the service request that
     * has been upvoted.
     * @param serviceRequestID the ID of the service request that just has been upvoted by the user
     */
    public void sendUpvoteToAPI(String serviceRequestID) {
        String extraDescription = "";
        RequestManager rManager = new RequestManager(this);
        rManager.upvoteServiceRequest(serviceRequestID, extraDescription);
    }

    /**
     * Add + 1 to the number of upvotes that are being shown on the UI.
     */
    public void addUpvoteToTextview() {
        String upvoteText = numbOfUpvoted.getText().toString();
        Integer updatedUpvoteNumb = Integer.parseInt(upvoteText) + 1;
        numbOfUpvoted.setText(String.valueOf(updatedUpvoteNumb));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
