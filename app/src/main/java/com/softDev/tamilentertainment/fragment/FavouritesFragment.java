package com.softDev.tamilentertainment.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.softDev.tamilentertainment.adapter.FavouriteAdapterList;
import com.softDev.tamilentertainment.model.FavouriteItem;
import com.softDev.tamilentertainment.R;
import com.softDev.tamilentertainment.util.SharedPreference;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavouritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavouritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavouritesFragment extends Fragment implements AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AdView mAdView;
    ListView favoriteList;
    private FavouriteAdapterList myAdapter;
    SharedPreference sharedPreference;
    List<FavouriteItem> favorites;
    private OnFragmentInteractionListener mListener;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavouritesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FavouritesFragment newInstance(String param1, String param2) {
        FavouritesFragment fragment = new FavouritesFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        sharedPreference = new SharedPreference();
        favorites = sharedPreference.getFavorites(getActivity());
        if (favorites == null) {
        } else {
            if (favorites.size() == 0) {
            }
            favoriteList = (ListView) rootView.findViewById(R.id.list);
            if (favorites != null) {
               myAdapter = new FavouriteAdapterList(getActivity(), favorites);
                favoriteList.setAdapter(myAdapter);
                favoriteList.setOnItemClickListener(this);
                favoriteList
                        .setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(
                                    AdapterView<?> parent, View view,
                                    int position, long id) {

                                ImageView button = (ImageView) view
                                        .findViewById(R.id.imgbtn_favorite);
                                String tag = button.getTag().toString();
                                if (tag.equalsIgnoreCase("grey")) {
                                    sharedPreference.addFavorite(getActivity(),
                                            favorites.get(position));
                                    Toast.makeText(getActivity(),"Added to favourites",Toast.LENGTH_SHORT).show();
                                    button.setTag("red");
                                    button.setImageResource(R.drawable.heart_red);
                                } else {
                                    sharedPreference.removeFavorite(getActivity(),
                                            favorites.get(position));
                                    button.setTag("grey");
                                    button.setImageResource(R.drawable.heart_grey);
                                   myAdapter.remove(favorites
                                            .get(position));
                                    Toast.makeText(getActivity(),"Remove from favourites",Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            }
                        });
            }
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
                    // Toast.makeText(getActivity().getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    //  Toast.makeText(getActivity().getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    // Toast.makeText(getActivity().getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
            mAdView.loadAd(adRequest);
        }
        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> a, View v, int position, long l) {

        FavouriteItem item = (FavouriteItem)a.getItemAtPosition(position);
        //Toast.makeText(getActivity(),String.valueOf(item.getId()), Toast.LENGTH_LONG).show();
        PlayerFragment.stationID = item.getId();
        PlayerFragment.isStationChanged = true;
        Fragment newPlayer = new LibraryFragment();
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        fm.findFragmentById(R.id.mainLayout);
        transaction.replace(R.id.main_container_wrapper,newPlayer);
        transaction.addToBackStack(null);
        transaction.commit();
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
        super.onDestroy();
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
