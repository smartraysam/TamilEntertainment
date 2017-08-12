package com.softDev.tamilentertainment.fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.softDev.tamilentertainment.model.FavouriteItem;
import com.softDev.tamilentertainment.util.LastFMCover;
import com.softDev.tamilentertainment.R;
import com.softDev.tamilentertainment.service.RadioService;
import com.softDev.tamilentertainment.util.SharedPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static android.content.Context.TELEPHONY_SERVICE;
import static com.softDev.tamilentertainment.activity.MainActivity.setTime;


public class PlayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    SharedPreference sharedPreference;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private NativeExpressAdView mAdView;
    private OnFragmentInteractionListener mListener;
    public static Button playButton;
    public static Button pauseButton;
    public static Button stopButton;
    public static Button nextButton;
    public static Button previousButton;
    public static Button favouriteButtonenable;
    public static Button favouriteButtondisable;
    public static Button onSleeptime;
    public static Button offSleeptime;
    public static ImageView stationImageView;

    public static TextView titleTextView;
    public static TextView sleepTime;
    public static TextView albumTextView;
    public static TextView artistTextView;
    public static TextView trackTextView;
    public static TextView statusTextView;
    public static TextView timeTextView;

    private Intent bindIntent;
    private TelephonyManager telephonyManager;
    private boolean wasPlayingBeforePhoneCall = false;
    public static RadioUpdateReceiver radioUpdateReceiver;
    public static RadioService radioService;
    private boolean isBound;
    private String STATUS_BUFFERING;
    private static final String TYPE_AAC = "aac";
    private static final String TYPE_MP3 = "mp3";
    private Handler handler;
    private AudioManager leftAm;
    private SeekBar volControl;
    private ContentObserver mVolumeObserver;
    View rootView;
    public static int stationID = 0;
    public static boolean isStationChanged = false;
    public static ArrayList<FavouriteItem> favourList = new ArrayList<>();
    public String StationNameID;
    String mDrawableName;
    VideoController mVideoController;
    int timerSet;
    public int resID, resID_default;
    CountDownTimer countSleepTime;
    private long mTimerRemaining;
    private static String LOG_TAG = "PlayerFragment";

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        sharedPreference = new SharedPreference();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_player, container, false);
        mAdView = (NativeExpressAdView) rootView.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device ID
                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d(LOG_TAG, "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        // Set an AdListener for the AdView, so the Activity can take action when an ad has finished
        // loading.
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d(LOG_TAG, "Received an ad that contains a video asset.");
                } else {
                    Log.d(LOG_TAG, "Received an ad that does not contain a video asset.");
                }
            }
        });

        mAdView.loadAd(adRequest);

        try {
            bindIntent = new Intent(getActivity(), RadioService.class);
            getActivity().bindService(bindIntent, radioConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {

        }

        telephonyManager = (TelephonyManager) getContext().getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        }

        handler = new Handler();

        try {

            STATUS_BUFFERING = getResources().getString(
                    R.string.status_buffering);
            playButton = (Button) rootView.findViewById(R.id.PlayButton);
            pauseButton = (Button) rootView.findViewById(R.id.PauseButton);
            stopButton = (Button) rootView.findViewById(R.id.StopButton);
            nextButton = (Button) rootView.findViewById(R.id.NextButton);
            previousButton = (Button) rootView.findViewById(R.id.PreviousButton);
            favouriteButtondisable = (Button) rootView.findViewById(R.id.favouritedisable);
            favouriteButtonenable = (Button) rootView.findViewById(R.id.favouriteImg);
            onSleeptime = (Button) rootView.findViewById(R.id.OnsetTime);
            offSleeptime = (Button) rootView.findViewById(R.id.offSetTime);
            sleepTime = (TextView) rootView.findViewById(R.id.sleepTimeval);
            onSleeptime.setEnabled(false);
            onSleeptime.setVisibility(View.INVISIBLE);
            pauseButton.setEnabled(false);
            //pauseButton.setVisibility(View.INVISIBLE);
            stationImageView = (ImageView) rootView.findViewById(R.id.stationImageView);
            playButton.setEnabled(true);
            stopButton.setEnabled(false);
            favouriteButtonenable.setEnabled(false);
            favouriteButtonenable.setVisibility(View.INVISIBLE);
            favouriteButtondisable.setEnabled(true);
            favouriteButtondisable.setVisibility(View.VISIBLE);
            titleTextView = (TextView) rootView.findViewById(R.id.titleTextView);
            albumTextView = (TextView) rootView.findViewById(R.id.albumTextView);
            artistTextView = (TextView) rootView.findViewById(R.id.artistTextView);
            trackTextView = (TextView) rootView.findViewById(R.id.trackTextView);
            statusTextView = (TextView) rootView.findViewById(R.id.statusTextView);
            timeTextView = (TextView) rootView.findViewById(R.id.timeTextView);
            // volume
            leftAm = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            int maxVolume = leftAm
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int curVolume = leftAm.getStreamVolume(AudioManager.STREAM_MUSIC);
            volControl = (SeekBar) rootView.findViewById(R.id.volumebar);
            volControl.setMax(maxVolume);
            volControl.setProgress(curVolume);
            volControl
                    .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                        @Override
                        public void onStopTrackingTouch(SeekBar arg0) {
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar arg0) {
                        }

                        @Override
                        public void onProgressChanged(SeekBar a0, int a1,
                                                      boolean a2) {
                            leftAm.setStreamVolume(AudioManager.STREAM_MUSIC,
                                    a1, 0);
                        }
                    });

            Handler mHandler = new Handler();
            // in onCreate put
            mVolumeObserver = new ContentObserver(mHandler) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);

                    if (volControl != null && leftAm != null) {
                        int volume = leftAm
                                .getStreamVolume(AudioManager.STREAM_MUSIC);
                        volControl.setProgress(volume);
                    }
                }

            };

            // vloume
            getActivity().startService(new Intent(getActivity().getApplicationContext(), RadioService.class));
            updateDefaultCoverImage();
            // updateFavourstate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioService.play();
                updateDefaultCoverImage();
                //updateFavourstate();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioService.stop();
                resetMetadata();
                updateDefaultCoverImage();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMetadata();
                playNextStation();
                updateDefaultCoverImage();
                // updateFavourstate();
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetMetadata();
                playPreviousStation();
                updateDefaultCoverImage();
                //updateFavourstate();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioService.pause();
            }
        });
        favouriteButtondisable.setOnClickListener(new View.OnClickListener() {
            int sID, iD;

            @Override
            public void onClick(View v) {
                StationNameID = radioService.getCurrentStationName();
                iD = radioService.getCurrentStationID();
                try {
                    String mDrawable = "station_"
                            + (radioService.getCurrentStationID() + 1);
                    sID = getResources().getIdentifier(mDrawable, "drawable",
                            getActivity().getPackageName());

                    int statD_default = getResources().getIdentifier("station_default",
                            "drawable", getActivity().getPackageName());
                    if (sID == 0)
                        sID = statD_default;


                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Toast.makeText(getActivity(), StationNameID+" "+ String.valueOf(sID)+" "+String.valueOf(iD), Toast.LENGTH_SHORT).show();
                FavouriteItem newItem = new FavouriteItem(StationNameID, sID, iD);
                if (checkFavoriteItem(newItem) == true) {
                    Toast.makeText(getActivity(), "Already Added to Favourite", Toast.LENGTH_SHORT).show();
                    favouriteButtonenable.setEnabled(true);
                    favouriteButtondisable.setEnabled(false);
                    favouriteButtondisable.setVisibility(View.INVISIBLE);
                    favouriteButtonenable.setVisibility(View.VISIBLE);
                } else {
                    sharedPreference.addFavorite(getActivity(), newItem);
                    Toast.makeText(getActivity(), "Added to Favourite", Toast.LENGTH_SHORT).show();
                    favouriteButtonenable.setEnabled(true);
                    favouriteButtondisable.setEnabled(false);
                    favouriteButtondisable.setVisibility(View.INVISIBLE);
                    favouriteButtonenable.setVisibility(View.VISIBLE);
                }

            }
        });
        favouriteButtonenable.setOnClickListener(new View.OnClickListener() {
            int sID, iD;

            @Override
            public void onClick(View v) {
                try {
                    String mDrawable = "station_"
                            + (radioService.getCurrentStationID() + 1);
                    sID = getResources().getIdentifier(mDrawable, "drawable",
                            getActivity().getPackageName());

                    int statD_default = getResources().getIdentifier("station_default",
                            "drawable", getActivity().getPackageName());
                    if (sID == 0)
                        sID = statD_default;


                } catch (Exception e) {
                    e.printStackTrace();
                }

                StationNameID = radioService.getCurrentStationName();
                iD = radioService.getCurrentStationID();
                FavouriteItem removeItem = new FavouriteItem(StationNameID, sID, iD);
                if (checkFavoriteItem(removeItem) == true) {
                    sharedPreference.removeFavorite(getActivity(), removeItem);
                    Toast.makeText(getActivity(), "Remove from Favourite", Toast.LENGTH_SHORT).show();
                    favouriteButtonenable.setEnabled(false);
                    favouriteButtondisable.setEnabled(true);
                    favouriteButtondisable.setVisibility(View.VISIBLE);
                    favouriteButtonenable.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Not in Favourites", Toast.LENGTH_SHORT).show();
                }

            }
        });
        offSleeptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  setTime= userInput.getText().toString();
                if (radioService.isPlaying() == true) {
                    timerSet = Integer.valueOf(setTime) * 60000;
                    if (mTimerRemaining > 0) {
                        mTimerRemaining = timerSet;
                    } else {
                        mTimerRemaining = timerSet;
                    }
                    // set sleep timer
                    startSleeptime(mTimerRemaining);
                    // start countdown
                    countSleepTime.start();
                    offSleeptime.setEnabled(false);
                    offSleeptime.setVisibility(View.INVISIBLE);
                    onSleeptime.setEnabled(true);
                    onSleeptime.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "No station playing", Toast.LENGTH_SHORT).show();
                    mTimerRemaining = 0;
                    sleepTime.setText("");
                    offSleeptime.setEnabled(true);
                    offSleeptime.setVisibility(View.VISIBLE);
                    onSleeptime.setEnabled(false);
                    onSleeptime.setVisibility(View.INVISIBLE);
                }

            }
        });
        onSleeptime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimerRemaining = 0;
                countSleepTime.cancel();
                sleepTime.setText("");
                offSleeptime.setEnabled(true);
                offSleeptime.setVisibility(View.VISIBLE);
                onSleeptime.setEnabled(false);
                onSleeptime.setVisibility(View.INVISIBLE);
            }
        });
        return rootView;
    }

    public void startSleeptime(long duration) {

        if (mTimerRemaining > 0 && countSleepTime != null) {
            countSleepTime.cancel();
            countSleepTime = null;
        }
        countSleepTime = new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                mTimerRemaining = millisUntilFinished;
                sleepTime.setText(getReadableTime(mTimerRemaining));
            }

            public void onFinish() {
                sleepTime.setText(" ");
                radioService.stop();
                resetMetadata();
                updateDefaultCoverImage();
            }
        };
    }

    private String getReadableTime(long remainingTime) {
        return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(remainingTime),
                TimeUnit.MILLISECONDS.toMinutes(remainingTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remainingTime)),
                TimeUnit.MILLISECONDS.toSeconds(remainingTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remainingTime)));
        //return (new SimpleDateFormat("hh:mm:ss")).format(new Date(remainingTime));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void updatePlayTimer() {
        timeTextView.setText(radioService.getPlayingTime());
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeTextView.setText(radioService.getPlayingTime());
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 1000);
    }

    public void playNextStation() {
        radioService.stop();
        radioService.setNextStation();
        /*
         * if(radioService.isPlaying()) {
		 * radioService.setStatus(STATUS_BUFFERING); updateStatus();
		 * radioService.stop(); radioService.play(); } else {
		 * radioService.stop(); }
		 */
    }

    public void playPreviousStation() {
        radioService.stop();
        radioService.setPreviousStation();
        /*
         * if(radioService.isPlaying()) {
		 * radioService.setStatus(STATUS_BUFFERING); updateStatus();
		 * radioService.stop(); radioService.play(); } else {
		 * radioService.stop(); }
		 */
    }

    public void updateDefaultCoverImage() {
        String currentStation = radioService.getCurrentStationName();
        try {
            mDrawableName = "station_"
                    + (radioService.getCurrentStationID() + 1);
            resID = getResources().getIdentifier(mDrawableName, "drawable",
                    getActivity().getPackageName());

            resID_default = getResources().getIdentifier("station_default",
                    "drawable", getActivity().getPackageName());
            if (resID > 0)
                stationImageView.setImageResource(resID);
            else {
                stationImageView.setImageResource(resID_default);
                resID = resID_default;
            }
            albumTextView.setText("");
            //  Toast.makeText(getActivity(),currentStation+" "+String.valueOf(radioService.getCurrentStationID())+" "+String.valueOf(resID), Toast.LENGTH_SHORT).show();
            FavouriteItem checkItem = new FavouriteItem(currentStation, resID, radioService.getCurrentStationID());
            if (checkFavoriteItem(checkItem) == true) {
                // Toast.makeText(getActivity(), "Favourite", Toast.LENGTH_SHORT).show();
                favouriteButtonenable.setEnabled(true);
                favouriteButtondisable.setEnabled(false);
                favouriteButtondisable.setVisibility(View.INVISIBLE);
                favouriteButtonenable.setVisibility(View.VISIBLE);

            } else {
                //  Toast.makeText(getActivity(), "Not Favourite", Toast.LENGTH_SHORT).show();
                favouriteButtonenable.setEnabled(false);
                favouriteButtondisable.setEnabled(true);
                favouriteButtonenable.setVisibility(View.INVISIBLE);
                favouriteButtondisable.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateAlbum() {
        String album = radioService.getAlbum();
        String artist = radioService.getArtist();
        String track = radioService.getTrack();
        Bitmap albumCover = radioService.getAlbumCover();

        albumTextView.setText(album);

        if (albumCover == null || (artist.equals("") && track.equals("")))
            updateDefaultCoverImage();
        else {
            stationImageView.setImageBitmap(albumCover);
            radioService.setAlbum(LastFMCover.album);

            if (radioService.getAlbum().length()
                    + radioService.getArtist().length() > 50) {
                albumTextView.setText("");
            }
        }
    }

    public void updateMetadata() {
        String artist = radioService.getArtist();
        String track = radioService.getTrack();
        // if(artist.length()>30)
        // artist = artist.substring(0, 30)+"...";
        artistTextView.setText(artist);
        trackTextView.setText(track);
        albumTextView.setText("");
    }

    public boolean checkFavoriteItem(FavouriteItem checkitem) {
        boolean check = false;
        List<FavouriteItem> favorites = sharedPreference.getFavorites(getContext());
        if (favorites != null) {
            for (FavouriteItem item : favorites) {
                if (item.equals(checkitem)) {
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    public void resetMetadata() {
        radioService.resetMetadata();
        artistTextView.setText("");
        albumTextView.setText("");
        trackTextView.setText("");
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

    @Override
    public void onPause() {
        super.onPause();
        if (mAdView != null) {
            mAdView.pause();
        }
        if (radioUpdateReceiver != null) {
            getActivity().unregisterReceiver(radioUpdateReceiver);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        // favourList=saveFavourite.loadFavorites(getContext());
        //  Toast.makeText(getActivity(), stationID+String.valueOf(isStationChanged), Toast.LENGTH_SHORT).show();
        // updateDefaultCoverImage();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (radioUpdateReceiver == null)
            radioUpdateReceiver = new RadioUpdateReceiver();
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_CREATED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_DESTROYED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_STARTED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_CONNECTING));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_START_PREPARING));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_PREPARED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_PLAYING));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_PAUSED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_STOPPED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_COMPLETED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_ERROR));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_BUFFERING_START));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_BUFFERING_END));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_METADATA_UPDATED));
        getActivity().registerReceiver(radioUpdateReceiver, new IntentFilter(
                RadioService.MODE_ALBUM_UPDATED));

        if (wasPlayingBeforePhoneCall) {
            radioService.play();
            wasPlayingBeforePhoneCall = false;
        }
        // Toast.makeText(getActivity(), stationID+ String.valueOf(isStationChanged), Toast.LENGTH_SHORT).show();
        if (radioService != null) {
            if (isStationChanged) {
                if (stationID != radioService.getCurrentStationID()) {
                    radioService.stop();
                    radioService.setCurrentStationID(stationID);
                    resetMetadata();
                    updateDefaultCoverImage();
                }
                if (!radioService.isPlaying())
                    radioService.play();
                isStationChanged = false;
            }

        }

    }

    private final ServiceConnection radioConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            radioService = ((RadioService.RadioBinder) service).getService();

            if (radioService.getTotalStationNumber() <= 1) {
                nextButton.setEnabled(false);
                //nextButton.setVisibility(View.INVISIBLE);
                previousButton.setEnabled(false);
                // previousButton.setVisibility(View.INVISIBLE);
            }

            updateStatus();
            updateMetadata();
            updateAlbum();
            updatePlayTimer();
            radioService.showNotification();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            radioService = null;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdView != null) {
            mAdView.destroy();
        }

        if (radioService != null) {
            if (!radioService.isPlaying() && !radioService.isPreparingStarted()) {
                // radioService.stopSelf();
                //radioService.stopService(bindIntent);
            }
        }

        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }

        unbindDrawables(rootView.findViewById(R.id.rootView));
        Runtime.getRuntime().gc();
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

    private class RadioUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(RadioService.MODE_CREATED)) {

            } else if (intent.getAction().equals(RadioService.MODE_DESTROYED)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                updateDefaultCoverImage();
                updateMetadata();
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_STARTED)) {
                pauseButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);

                playButton.setEnabled(true);
                stopButton.setEnabled(false);
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_CONNECTING)) {
                pauseButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                playButton.setEnabled(false);
                stopButton.setEnabled(true);
                updateStatus();
            } else if (intent.getAction().equals(
                    RadioService.MODE_START_PREPARING)) {
                pauseButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                playButton.setEnabled(false);
                stopButton.setEnabled(true);
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_PREPARED)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                //  pauseButton.setVisibility(View.INVISIBLE);
                updateStatus();
            } else if (intent.getAction().equals(
                    RadioService.MODE_BUFFERING_START)) {
                updateStatus();
            } else if (intent.getAction().equals(
                    RadioService.MODE_BUFFERING_END)) {
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_PLAYING)) {
                if (radioService.getCurrentStationType().equals(TYPE_AAC)) {
                    playButton.setEnabled(false);
                    stopButton.setEnabled(true);
                } else {
                    playButton.setEnabled(false);
                    pauseButton.setEnabled(true);
                    stopButton.setEnabled(true);
                    // playButton.setVisibility(View.INVISIBLE);
                    pauseButton.setVisibility(View.VISIBLE);
                }
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_PAUSED)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(true);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_STOPPED)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_COMPLETED)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                // radioService.setCurrentStationURL2nextSlot();
                // radioService.play();
                updateStatus();
            } else if (intent.getAction().equals(RadioService.MODE_ERROR)) {
                playButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                playButton.setVisibility(View.VISIBLE);
                // pauseButton.setVisibility(View.INVISIBLE);
                updateStatus();
            } else if (intent.getAction().equals(
                    RadioService.MODE_METADATA_UPDATED)) {
                updateMetadata();
                updateStatus();
                updateDefaultCoverImage();
            } else if (intent.getAction().equals(
                    RadioService.MODE_ALBUM_UPDATED)) {
                updateAlbum();
            }
        }
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                wasPlayingBeforePhoneCall = radioService.isPlaying();
                radioService.stop();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (wasPlayingBeforePhoneCall) {
                    radioService.play();
                }
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                // A call is dialing,
                // active or on hold
                wasPlayingBeforePhoneCall = radioService.isPlaying();
                radioService.stop();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    public void updateStatus() {
        String status = radioService.getStatus();
        if (radioService.getTotalStationNumber() > 1) {
			/*
			 * if (status != "") status = radioService.getCurrentStationName() +
			 * " - " + status; else status =
			 * radioService.getCurrentStationName();
			 */
            String title = radioService.getCurrentStationName();
            try {
                titleTextView.setText(title);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        try {
            statusTextView.setText(status);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
