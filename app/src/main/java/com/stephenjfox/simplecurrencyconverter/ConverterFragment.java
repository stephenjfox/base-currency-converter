package com.stephenjfox.simplecurrencyconverter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.MessageFormat;
import java.util.Locale;

/**
 * Fragment that hosts the view of a currency converter
 * <p>
 * Uses HTTP to pull the currency rates from {@literal http://api.fixer.io/latest}.
 * It's accuracy is 99.85% of the global currency converter on CNN Money.
 * <p>
 * <p>
 * Currently only implemented for USD <-> KRW.
 * <p>
 * Created by Stephen on 9/24/2016.
 */

public class ConverterFragment extends Fragment {

  /*
    public static final Retrofit retroFit = new Retrofit.Builder()
        .baseUrl("http://api.fixer.io")
        .build();
  */
  private static final String TAG = ConverterFragment.class.getName();

  // to do a conversion
  private Button mUpdateButton;

  // Our spinners that determine our conversion
  private Spinner mTopSpinner;
  private Spinner mBottomSpinner;

  // The fields for our text
  private EditText mTopTextField;
  private EditText mBottomTextField;

  // For tracking which to serve as our reference point
  private EditText mLastEdited;
  private boolean mHasInternet;

  @Nullable
  @Override
  public View onCreateView(final LayoutInflater inflater,
                           @Nullable final ViewGroup container,
                           @Nullable final Bundle savedInstanceState) {
    final View inflate = inflater.inflate(R.layout.fragment_main, container, false);

    // initialize our Spinner-Labels
    mTopSpinner = (Spinner) inflate.findViewById(R.id.usLabel);
    mTopSpinner.setAdapter(new ArrayAdapter<>(getContext(),
        android.R.layout.simple_spinner_dropdown_item, CurrencyRate.values()));
    mTopSpinner.setSelection(CurrencyRate.USD.ordinal()); // default to USD

    mBottomSpinner = (Spinner) inflate.findViewById(R.id.koLabel);
    mBottomSpinner.setAdapter(new ArrayAdapter<>(getContext(),
        android.R.layout.simple_spinner_dropdown_item, CurrencyRate.values()));
    mBottomSpinner.setSelection(CurrencyRate.KRW.ordinal());

    // Inflate our input fields
    mTopTextField = (EditText) inflate.findViewById(R.id.usInput);
    mTopTextField.addTextChangedListener(new PassThroughListener(mTopTextField));

    mBottomTextField = (EditText) inflate.findViewById(R.id.koInput);
    mBottomTextField.addTextChangedListener(new PassThroughListener(mBottomTextField));

    // Buttons
    mUpdateButton = (Button) inflate.findViewById(R.id.updateButton);

    applyButtonClickListeners();

    return inflate;
  }

  private void applyButtonClickListeners() {

    mUpdateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(final View v) {
        if (mLastEdited != null) {

          if (mLastEdited == mTopTextField) {
            // convert from top to down - because we don't want to overwrite the user's input
            // as that would cause frustration
            final String topFloatString = mTopTextField.getText().toString();
            final double sourceDouble = Double.parseDouble(topFloatString);

            final double presentation = calculateConversion(sourceDouble,
                (CurrencyRate) mTopSpinner.getSelectedItem(), (CurrencyRate) mBottomSpinner.getSelectedItem());

            mBottomTextField.setText(String.format(Locale.getDefault(), "%f", presentation));

          } else {
            // otherwise, it's the bottom input and we should convert from top to bottom
            final String bottomFloatString = mBottomTextField.getText().toString();
            final double source = Double.parseDouble(bottomFloatString);

            final double presentation = calculateConversion(source,
                (CurrencyRate) mBottomSpinner.getSelectedItem(), (CurrencyRate) mTopSpinner.getSelectedItem());
            mTopTextField.setText(String.format(Locale.getDefault(), "%f", presentation));
          }

        }
      }
    });

  }

  private double calculateConversionFactor(final CurrencyRate leftRate,
                                           final CurrencyRate rightRate) {
    return  leftRate.getFactor() / rightRate.getFactor();
  }

  private double calculateConversion(final double source,
                                     final CurrencyRate firstRate,
                                     final CurrencyRate secondRate) {
    return source / calculateConversionFactor(firstRate, secondRate);
  }

  private class PassThroughListener implements TextWatcher {

    private final EditText invoker;

    PassThroughListener(final EditText invoker) {
      this.invoker = invoker;
    }

    @Override
    public void beforeTextChanged(final CharSequence s,
                                  final int start,
                                  final int count,
                                  final int after) {
      // omitted intentionally
    }

    @Override
    public void onTextChanged(final CharSequence s,
                              final int start,
                              final int before,
                              final int count) {
      // omitted intentionally
    }

    @Override
    public void afterTextChanged(final Editable s) {
      if (s.length() != 0) {
        mLastEdited = invoker;
      }
    }
  }

  private enum CurrencyRate {

    AUD(1.4685),

    BGN(1.9558),

    BRL(3.5931),

    CAD(1.4625),

    CHF(1.0887),

    CNY(7.4797),

    CZK(27.022),

    DKK(7.4552),

    GBP(0.86435),

    HKD(8.6979),

    HRK(7.5005),

    HUF(305.51),

    IDR(14663.99),

    ILS(4.2161),

    INR(74.711),

    JPY(113.02),

    KRW(1235.78),

    MXN(22.0086),

    MYR(4.6028),

    NOK(9.1028),

    NZD(1.5435),

    PHP(53.881),

    PLN(4.2869),

    RON(4.4475),

    RUB(71.2201),

    SEK(9.582),

    SGD(1.5231),

    THB(38.834),

    TRY(3.3059),

    USD(1.1214),

    ZAR(15.237);

    private final double factor;

    CurrencyRate(final double factor) {
      this.factor = factor;
    }

    public double getFactor() {
      return factor;
    }
  }
}
