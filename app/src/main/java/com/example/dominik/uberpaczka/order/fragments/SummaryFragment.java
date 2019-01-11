package com.example.dominik.uberpaczka.order.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.dominik.uberpaczka.R;
import com.example.dominik.uberpaczka.maps.MapsActivity;
import com.example.dominik.uberpaczka.order.usable.DistanceLoader;
import com.example.dominik.uberpaczka.order.usable.OrderInfo;
import com.example.dominik.uberpaczka.utils.Checker;
import com.example.dominik.uberpaczka.utils.Editable;
import com.example.dominik.uberpaczka.validators_patterns.PhoneValidator;
import com.example.dominik.uberpaczka.validators_patterns.Validable;

/**
 * Created by marek on 09.01.2019.
 */

public class SummaryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Long>, Editable
        , Validable {


    private final int LOADER_ID = 1;
    private OrderInfo orderInfo;
    private TextInputLayout receipientData;
    private TextInputLayout receipientPhone;
    private PhoneValidator phoneValidator;
    private Button orderButton;
    private TextView pickUpTextView;
    private TextView destinationTextView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Bundle bundle = getArguments();
        orderInfo = (OrderInfo) bundle.getSerializable("order_info");


        final View view = inflater.inflate(R.layout.fragment_summary, container, false);


        Toolbar toolbar = view.findViewById(R.id.toolbar_ordersummary);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
        receipientData = view.findViewById(R.id.textinput_receipient_name);
        receipientPhone = view.findViewById(R.id.textinput_receipient_phone_number);
        orderButton = view.findViewById(R.id.acceptance_button);
        pickUpTextView = view.findViewById(R.id.pickup_textview);
        destinationTextView = view.findViewById(R.id.destination_textview);
        pickUpTextView.setText(orderInfo.getFrom());
        destinationTextView.setText(orderInfo.getDestination());
        createValidationPatterns();
        setUpButtonListeners();
        getLoader();


        return view;


    }


    @Override
    public void createValidationPatterns() {
        phoneValidator = new PhoneValidator(receipientPhone, getString(R.string.error_phone_number), getString(R.string.error_blank));
    }

    @Override
    public void setUpButtonListeners() {

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (Checker.checkInternetConnection(getContext(), getFragmentManager())) {

                    }
                }
            }
        });
    }

    @Override
    public boolean validate() {
        receipientData.setError(null);
        boolean receipientEmpty = TextUtils.isEmpty(receipientData.getEditText().getText().toString());
        if (receipientEmpty) {
            receipientData.setError(getString(R.string.error_blank));
        }
        return phoneValidator.validate() & !receipientEmpty;
    }

    @NonNull
    @Override
    public Loader<Long> onCreateLoader(int i, @Nullable Bundle bundle) {

        Context context = getContext();
        if (context != null) {
            return new DistanceLoader(context, orderInfo.getFrom(), orderInfo.getDestination());
        }

        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Long> loader, Long aLong) {

        Log.i("Loader", "Finished working");
        updateUI(aLong);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Long> loader) {
        updateUI(Long.valueOf(0));
    }


    public void updateUI(Long aLong) {
        TextView textView = getView().findViewById(R.id.amount_price_textview);
        if (textView != null)
            textView.setText(aLong.toString());
    }


    public void getLoader() {
        if (getActivity().getSupportLoaderManager().getLoader(LOADER_ID) == null)
            getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        else
            getActivity().getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    public void closeFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
//                        .setCustomAnimations(R.anim, R.anim.push_up_out)
                .remove(this)
                .commit();

        ((MapsActivity) getActivity()).showPlaceAutoCompletePickUpFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoader();

    }

    public void stopLoader() {
        getLoaderManager().destroyLoader(LOADER_ID);
    }
}