package com.softDev.tamilentertainment.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.softDev.tamilentertainment.adapter.DrawableListAdapter;
import com.softDev.tamilentertainment.R;
import com.softDev.tamilentertainment.service.RadioService;

public class StationsFragment extends Fragment implements OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AdView mAdView;
    View rootView;
    private Handler handler;
    boolean changeStation;
    int StationId;
    private GridView gridView;
    private TextView empty;
    private ProgressBar progressBar;
    private DrawableListAdapter mAdapter;
    private FrameLayout layout;
    private Intent bindIntent;
    private RadioService radioService;


    private OnFragmentInteractionListener mListener;

    public StationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationsFragment newInstance(String param1, String param2) {
        StationsFragment fragment = new StationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stations, container, false);
        mAdView = (AdView) rootView.findViewById(R.id.adView);

        //loadList();
        handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // getActivity().setContentView(R.layout.fragment_stations);
                            loadList();
                        } catch (Exception e) {
                            getActivity().finish();
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                //  Toast.makeText(getActivity().getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Toast.makeText(getActivity().getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getActivity().getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
        return rootView;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
        PlayerFragment.stationID = position;
        PlayerFragment.isStationChanged = true;
       // Intent intent = new Intent(getActivity(), MainActivity.class);
       // startActivity(intent);
        Fragment newPlayer = new LibraryFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fm.findFragmentById(R.id.mainLayout);
        transaction.replace(R.id.main_container_wrapper,newPlayer);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void loadList() {
        gridView = (GridView) rootView.findViewById(R.id.list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.load);
        empty = (TextView) rootView.findViewById(R.id.empty);
        layout = (FrameLayout) rootView.findViewById(R.id.list_show);
        gridView.setOnItemClickListener(this);
        progressBar.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        // layout.setVisibility(View.GONE);
        // mAdapter = new DrawableListAdapter(getActivity().getApplicationContext());
        //listView.setAdapter(mAdapter);
        // layout.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdapter = new DrawableListAdapter(getContext());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setAdapter(mAdapter);
                        progressBar.setVisibility(View.GONE);
                        layout.setVisibility(View.VISIBLE);
                        if (mAdapter.getCount() == 0) {
                            gridView.setVisibility(View.GONE);
                            empty.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();

    }

    private void unbindDrawables(View view) {
        try {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /**
         if (context instanceof OnFragmentInteractionListener) {
         mListener = (OnFragmentInteractionListener) context;
         } else {
         throw new RuntimeException(context.toString()
         + " must implement OnFragmentInteractionListener");
         }
         **/
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }


        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        Runtime.getRuntime().gc();
        super.onDestroy();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
