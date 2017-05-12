package com.example.justin.verbeterjegemeente;

//Deze testklasse werkt niet
// Mockito moet nog bekeken worden voor mocken

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import android.content.Context;
import android.content.SharedPreferences;

@RunWith(MockitoJUnitRunner.class)
public class MockitoTest {

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mMockContext;

    @Test
    public void readStringFromContext_LocalizedString() {
        // Given a mocked Context injected into the object under test...
        when(mMockContext.getString(R.string.beschrijving))
                .thenReturn(FAKE_STRING);
        ClassUnderTest myObjectUnderTest = new ClassUnderTest(mMockContext);

        // ...when the string is returned from the object under test...
        String result = myObjectUnderTest.getHelloWorldString();

        // ...then the result should be the expected one.
        assertThat(result, is(FAKE_STRING));
    }
}

class ClassUnderTest {
    Context context;

    ClassUnderTest(Context context) {
        this.context = context;
    }

   public String getHelloWorldString(){
        return context.getString(R.string.beschrijving);
    }
}