package com.example.justin.verbeterjegemeente;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v4.content.ContextCompat;
import android.test.ActivityUnitTestCase;
import android.view.View;
import android.widget.Button;

import com.example.justin.verbeterjegemeente.domain.Melding;

import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import static android.R.attr.button;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.justin.verbeterjegemeente.EspressoTestsMatchers.noDrawable;
import static com.example.justin.verbeterjegemeente.EspressoTestsMatchers.withDrawable;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

@RunWith(AndroidJUnit4.class)
public class CameraTest extends ActivityInstrumentationTestCase2<MeldingActivity> {

    public CameraTest()
    {
        super(MeldingActivity.class);
    }
    @Rule
    public IntentsTestRule<MeldingActivity> intentsRule = new IntentsTestRule<>(MeldingActivity.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void validateCameraScenario() {
        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
                InstrumentationRegistry.getTargetContext().getResources(),
                R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera, this tells Espresso to respond
        // with the ActivityResult we just created
        intending(toPackage("com.android.camera")).respondWith(result);


//        naar melding maken gaan
//        ...

//        foto toevoegen knop is aanwezig
        onView(withText(R.string.foto)).check(matches(isDisplayed()));
//        controleren of de image overeenkomt met standaard foto en standaard button tekst te lezen is
        onView(withId(R.id.fotoImageView)).check(matches(withDrawable(R.drawable.imageicon)));

//        Klikken om foto te maken
        onView(withId(R.id.fotoButton)).perform(click());
        onView(withText("Foto maken")).perform(click());
//        Controleren of camera gebruikt is
        intended(toPackage("com.android.camera"));
//        controleren of de image niet meer overeenkomt met standaard foto nadat foto genomen is
        onView(withId(R.id.fotoImageView)).check(matches(not(withDrawable(R.drawable.imageicon))));

//        Klikken op knoppen/dialog
        onView(withId(R.id.fotoButton)).perform(click());
        onView(withText("Kies bestaande foto")).perform(click());
//       werkt niet --  onView(withText("Pictures")).perform(click());

    }
}

 class DrawableMatcher extends TypeSafeMatcher<View> {

    private final int expectedId;
    String resourceName;

    public DrawableMatcher(int expectedId) {
        super(View.class);
        this.expectedId = expectedId;
    }

    @Override
    protected boolean matchesSafely(View target) {
        if (!(target instanceof ImageView)){
            return false;
        }
        ImageView imageView = (ImageView) target;
        if (expectedId < 0){
            return imageView.getDrawable() == null;
        }
        Resources resources = target.getContext().getResources();
        Drawable expectedDrawable = resources.getDrawable(expectedId);
        resourceName = resources.getResourceEntryName(expectedId);

        if (expectedDrawable == null) {
            return false;
        }

        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap otherBitmap = ((BitmapDrawable) expectedDrawable).getBitmap();
        return bitmap.sameAs(otherBitmap);
    }

    @Override
    public void describeTo(org.hamcrest.Description description) {
        description.appendText("with drawable from resource id: ");
        description.appendValue(expectedId);
        if (resourceName != null) {
            description.appendText("[");
            description.appendText(resourceName);
            description.appendText("]");
        }
    }
}

class EspressoTestsMatchers {

    public static Matcher<View> withDrawable(final int resourceId) {
        return new DrawableMatcher(resourceId);
    }

    public static Matcher<View> noDrawable() {
        return new DrawableMatcher(-1);
    }
}
