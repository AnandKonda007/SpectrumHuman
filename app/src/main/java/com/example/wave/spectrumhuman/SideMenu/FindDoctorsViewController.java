package com.example.wave.spectrumhuman.SideMenu;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wave.spectrumhuman.HomeModule.SideMenuViewController;
import com.example.wave.spectrumhuman.Languages.LanguageTextController;
import com.example.wave.spectrumhuman.Languages.LanguagesKeys;
import com.example.wave.spectrumhuman.LoginModule.LocationTracker;
import com.example.wave.spectrumhuman.R;
import com.example.wave.spectrumhuman.googlemaps.Result;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Rise on 20/12/2017.
 */

public class FindDoctorsViewController extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private RecyclerView horizontal_recycler_view;
    private HorizontalAdapter horizontalAdapter;


    public static Dialog dialog;

    Location mLastLocation;
    Marker mCurrLocationMarker;

    LatLng selectedLocation;
    RelativeLayout relativeLayout;
    Button direction;

    SupportMapFragment mapFrag;
    TextView addressTextView, distanceTextView, eTATextView, location, address, distance;
    TextView txt_findHospital;
    ImageView search, back, img_doctorEmail, img_doctorPhone, img_doctorinfo;
    int selectedPosition = -1;
    TextView txt_findhospital;
     String objPhone;
    String objmailOrWeb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finddoctor);

        LocationTracker.getInstance().refreshLocations();

        showRefreshDialogue();
        init();
        updateLanguageTexts();

    }
    private void init() {

        txt_findHospital = (TextView) findViewById(R.id.txt_findhospital);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemaps);
        mapFrag.getMapAsync(this);


        horizontal_recycler_view = (RecyclerView) findViewById(R.id.horizontal_recycler_view);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativelocation);
        direction = (Button) findViewById(R.id.directions);
        search = (ImageView) findViewById(R.id.imagesearch);
        back = (ImageView) findViewById(R.id.imageback);
        addressTextView = (TextView) findViewById(R.id.editaddress);
        distanceTextView = (TextView) findViewById(R.id.editdistance);
        eTATextView = (TextView) findViewById(R.id.editestimatetime);
        location = (TextView) findViewById(R.id.location);
        address = (TextView) findViewById(R.id.address);
        distance = (TextView) findViewById(R.id.distance);


        horizontalAdapter = new HorizontalAdapter(getApplication());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(FindDoctorsViewController.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManager);
        horizontal_recycler_view.setAdapter(horizontalAdapter);
        horizontal_recycler_view.setHasFixedSize(true);

        img_doctorEmail = (ImageView) findViewById(R.id.doctor_email);
        img_doctorPhone = (ImageView) findViewById(R.id.doctor_phone);
        img_doctorinfo = (ImageView) findViewById(R.id.doctor_info);


        img_doctorEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMessageAlert();
            }
        });

        img_doctorPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallAlert();

            }
        });

        img_doctorinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String placeId = GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getPlaceId();

                if (placeId != null) {
                    Log.e("placeId", "call" + placeId);
                    Uri uri = Uri.parse("https://www.google.com/maps/place/?q=place_id:" + placeId); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            }
        });
        direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String originLocation = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()) + "," + String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
                String destinationLocation = String.valueOf(selectedLocation.latitude) + "," + String.valueOf(selectedLocation.longitude);

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + originLocation + "&daddr=" + destinationLocation + "&mode=d"));
                startActivity(intent);

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGooglePlaceSelection();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    /////////UPDATELANGUAGES///////////////
    public void updateLanguageTexts() {

        distance.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DISTANCE_KEY));
        address.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.ADDRESS_KEY));

        location.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.LOCATION_KEY));
        direction.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.DIRECTIONS_KEY));
        txt_findHospital.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.FIND_DOCTOR_KEY));
    }

    private void dropPin(String title) {
        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(selectedLocation);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
        //  markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13));


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        /////////
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();


            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            //mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Place current location marker
        selectedLocation = new LatLng(LocationTracker.getInstance().currentLocation.getLatitude(), LocationTracker.getInstance().currentLocation.getLongitude());
       /* MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);*/
        //move map camera
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 11));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        loadPlaces(true);
        dialog.dismiss();

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e("MapsFailue", connectionResult.getErrorMessage());

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(FindDoctorsViewController.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
        }
    }


    /////////////////////////// Horozontal recyclerview/////////

    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {


        Context context;


        public HorizontalAdapter(Context context) {
            this.context = context;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView, img_rating;
            TextView txtview, textrating, txt_specialization, txt_rating;

            public MyViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.imageview);
                txtview = (TextView) view.findViewById(R.id.txtview);
                textrating = (TextView) view.findViewById(R.id.textrating);
                txt_specialization = (TextView) view.findViewById(R.id.txtspecialization);
                img_rating = (ImageView) view.findViewById(R.id.image_rating);
                txt_rating = (TextView) view.findViewById(R.id.textrating);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_horizontalrecyclerview, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            final Result objPlaceResult = GooglePlacesFinder.getInstance().placesArray.get(position);
            holder.txtview.setText(objPlaceResult.getName());
            holder.imageView.setImageResource(R.drawable.ic_doctorlist);
            holder.txt_specialization.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(objPlaceResult.getSpecilization()));

            if (holder.txt_specialization.getText().toString().isEmpty()) {
                Log.e("emptySpecilization", "call");
            } else {
                Log.e("Specilization", "call" + objPlaceResult.getSpecilization());
                holder.txt_rating.setVisibility(View.INVISIBLE);
                holder.img_rating.setVisibility(View.INVISIBLE);
            }

            if (selectedPosition == position)
                holder.itemView.setBackgroundResource(R.drawable.googlemapsselected);
            else
                holder.itemView.setBackgroundResource(R.drawable.googlemapsdefault);

            if (objPlaceResult.getRating() != null) {
                holder.textrating.setText(String.valueOf(objPlaceResult.getRating()));
            }

            if (objPlaceResult.getPhotos() != null && objPlaceResult.getPhotos().size() > 0) {
                String photoReference = objPlaceResult.getPhotos().get(0).getPhotoReference();
                String urlString = GooglePlacesFinder.getInstance().googlePhotoURL(photoReference, 300);
                Picasso.with(context).load(urlString).into(holder.imageView);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    //
                    selectedPosition = position;
                    relativeLayout.setVisibility(View.VISIBLE);
                    clearDataForNewRoute();
                    final Result selectedResult = GooglePlacesFinder.getInstance().placesArray.get(position);
                    selectedLocation = new LatLng(selectedResult.getGeometry().getLocation().getLat(), selectedResult.getGeometry().getLocation().getLng());
                    dropPin(selectedResult.getName());
                    Log.e("placeids", "call" + selectedResult.getPlaceId());
                    addressTextView.setText(selectedResult.getVicinity());
                    String originLocation = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()) + "," + String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
                    String destinationLocation = String.valueOf(selectedLocation.latitude) + "," + String.valueOf(selectedLocation.longitude);
                    GooglePlacesFinder.getInstance().getDirectionsInformation(originLocation, destinationLocation);

                    if (selectedResult.getFromGoogle()) {

                        img_doctorinfo.setVisibility(View.VISIBLE);
                        HandlerThread handlerThread = new HandlerThread("fetchDoctor");
                        handlerThread.start();
                        Handler handler = new Handler(handlerThread.getLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                fetchPlaceDetailsFromGoogleMaps(selectedResult);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        checkHospitalHavingUrlandPhone(selectedPosition);

                                    }
                                });
                            }
                        });

                    } else {

                        Log.e("objPhone", "call" + objPhone);
                        img_doctorPhone.setVisibility(View.VISIBLE);
                        img_doctorEmail.setVisibility(View.VISIBLE);
                        img_doctorEmail.setBackgroundResource(R.drawable.ic_doctoremail);
                        img_doctorinfo.setVisibility(View.GONE);

                    }
                    notifyDataSetChanged();
                }

            });
            if (position == GooglePlacesFinder.getInstance().placesArray.size() - 1) {
                loadPlaces(false);
            }

        }

        @Override
        public int getItemCount() {
            return GooglePlacesFinder.getInstance().placesArray.size();
        }
    }

    public void checkHospitalHavingUrlandPhone(int position) {
        Log.e("checkHospitalHavingUrlOrPhone", "call");

        if (GooglePlacesFinder.getInstance().placesArray.get(position).getFormattedPhoneNumber() != null) {
            img_doctorPhone.setVisibility(View.VISIBLE);

        } else {
            img_doctorPhone.setVisibility(View.GONE);
        }
        if (GooglePlacesFinder.getInstance().placesArray.get(position).getWebsite() != null) {
            img_doctorEmail.setVisibility(View.VISIBLE);
            img_doctorEmail.setBackgroundResource(R.drawable.ic_web);
        } else {
            img_doctorEmail.setVisibility(View.GONE);
        }

    }

    public void fetchPlaceDetailsFromGoogleMaps(final Result selectedresult) {
        ExecutorService taskExecutor = Executors.newFixedThreadPool(1);
        try {
            Runnable backgroundTask = new Runnable() {
                @Override
                public void run() {
                    BlockingQueue<Result> blockingQueue = GooglePlacesFinder.getInstance().getPlaceDetails(selectedresult);
                    try {
                        Log.e("PhoneNumber", "" + blockingQueue.take().getFormattedPhoneNumber());
                        Log.e("blockingQueue", "" + selectedresult.getWebsite());
                        Log.e("Open_now", "" + selectedresult.getOpen_now());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            };
            taskExecutor.submit(backgroundTask);
            taskExecutor.shutdown();
            taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            if (selectedresult.getFormattedPhoneNumber() != null || selectedresult.getWebsite() != null) {
                GooglePlacesFinder.getInstance().placesArray.set(selectedPosition, selectedresult);
            } else {
            }
        } catch (InterruptedException e) {

        }
    }

    private void clearDataForNewRoute() {

        distanceTextView.setText("");
        eTATextView.setText("");
        addressTextView.setText("");
        GooglePlacesFinder.getInstance().etaTimeString = "";
        GooglePlacesFinder.getInstance().selectedRoute = null;
        GooglePlacesFinder.getInstance().distanceString = "";
        GooglePlacesFinder.getInstance().encodedRoutePath = null;
        GooglePlacesFinder.getInstance().overviewPolyline = null;


    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SideMenuViewController.MessageEvent event) {
        Log.e("sidemenuMessageevent", "" + event.message);
        String resultData = event.message.trim();

        if (resultData.equals("GooglePlacesSuccess")) {

            Log.e("resultDataSuccess", "" + resultData);
            Log.e("LocationInfo", "" + selectedLocation.latitude);
            Log.e("LocationInfo", "" + selectedLocation.longitude);

            GooglePlacesFinder.getInstance().getPlacesInformationFromOurCloud(selectedLocation.latitude, selectedLocation.longitude, 5000);

        } else if (resultData.equals("GooglePlacesFailure")) {
            Log.e("resultData", "" + resultData);

            GooglePlacesFinder.getInstance().getPlacesInformationFromOurCloud(selectedLocation.latitude, selectedLocation.longitude, 5000);

        } else if (resultData.equals("GoogleRoutesSuccess")) {

            Log.e("RoutesInfo", "success");
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.addAll(PolyUtil.decode(GooglePlacesFinder.getInstance().encodedRoutePath));
            polyOptions.color(this.getResources().getColor(R.color.colororange));
            polyOptions.width(10.0f);
            mMap.addPolyline(polyOptions);

            distanceTextView.setText(GooglePlacesFinder.getInstance().distanceString);
            eTATextView.setText(GooglePlacesFinder.getInstance().etaTimeString);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (LatLng latLng : polyOptions.getPoints()) {
                builder.include(latLng);
            }

            LatLngBounds bounds = builder.build();

            int padding = 0; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            // For animate
            mMap.animateCamera(cu);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 12));

            // Without animate
            //mMap.moveCamera(cu);

        } else if (resultData.equals("GoogleRoutesFailure")) {

            Log.e("RoutesInfo", "failed");

        } else if (resultData.equals("PlacesSuccess")) {

            horizontalAdapter.notifyDataSetChanged();


        } else if (resultData.equals("PlacesFailure")) {

            Log.e("RoutesInfo", "failed");
            horizontalAdapter.notifyDataSetChanged();


        }
    }


    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);


    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }


    public boolean canLoadMore() {
        if (GooglePlacesFinder.getInstance().isLoading) {
            return false;
        }
        if (!GooglePlacesFinder.getInstance().canLoadMore()) {
            return false;
        }

        return true;
    }

    public void loadPlaces(Boolean force) {

        if (!force) {

            if (!canLoadMore()) {
                return;
            }


        }
        GooglePlacesFinder.getInstance().isLoading = true;
        GooglePlacesFinder.getInstance().getPlacesInformation("hospital", selectedLocation.latitude, selectedLocation.longitude, 5000);
    }


    public void openGooglePlaceSelection() {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mMap.clear();
                clearDataForNewRoute();
                relativeLayout.setVisibility(View.VISIBLE);
                Place place = PlaceAutocomplete.getPlace(this, data);
                selectedLocation = place.getLatLng();
                dropPin(""+place.getName());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 11));
                GooglePlacesFinder.getInstance().isLoading = true;
                GooglePlacesFinder.getInstance().status = null;
                GooglePlacesFinder.getInstance().nextPageToken = null;
                GooglePlacesFinder.getInstance().placesArray.clear();
                horizontalAdapter.notifyDataSetChanged();
                loadPlaces(true);
                ////
                dropPin(""+place.getName());
                addressTextView.setText(place.getAddress());
                String originLocation = String.valueOf(LocationTracker.getInstance().currentLocation.getLatitude()) + "," + String.valueOf(LocationTracker.getInstance().currentLocation.getLongitude());
                String destinationLocation = String.valueOf(selectedLocation.latitude) + "," + String.valueOf(selectedLocation.longitude);
                GooglePlacesFinder.getInstance().getDirectionsInformation(originLocation, destinationLocation);

                Log.e("placegetId", "call" + place.getId()+""+place.getWebsiteUri()+""+place.getPhoneNumber());

                loadPhoneNumberAndWebsite(place);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
  public void loadPhoneNumberAndWebsite(final Place place){

      String strPhone= String.valueOf(place.getPhoneNumber().toString());
      Uri uri=place.getWebsiteUri();
      Log.e("WebsiteUri","call"+uri);


      if (!strPhone.isEmpty()){

          img_doctorPhone.setVisibility(View.VISIBLE);
          img_doctorPhone.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  loadPhoneAlert(""+place.getPhoneNumber());
              }
          });

          }else {
          Log.e("elseNumber","call");
          img_doctorPhone.setVisibility(View.GONE);
      }

          if (uri == null ){
              Log.e("elseweb","call");

              img_doctorEmail.setVisibility(View.GONE);
          }else {

              final String strWeb= String.valueOf(uri.toString());

              img_doctorEmail.setVisibility(View.VISIBLE);
              img_doctorEmail.setBackgroundResource(R.drawable.ic_web);

              img_doctorEmail.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      Uri uri = Uri.parse(strWeb);// missing 'http://' will cause crashed
                      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                      startActivity(intent);
                  }
              });
          }
         img_doctorinfo.setVisibility(View.VISIBLE);
         img_doctorinfo.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (place.getId() != null) {
                     Log.e("placeId", "call" + place.getId());
                     Uri uri = Uri.parse("https://www.google.com/maps/place/?q=place_id:" + place.getId()); // missing 'http://' will cause crashed
                     Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                     startActivity(intent);
                 }

             }
         });

  }
    ImageView imageView;
    Handler handler = new Handler();

    public void showRefreshDialogue() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_animate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        //dialog.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        imageView = (ImageView) dialog.findViewById(R.id.image_rottate);
        handler.postDelayed(updateTimerThread, 100);
    }

    public Runnable updateTimerThread = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            imageView.setVisibility(View.VISIBLE);
            handler.postDelayed(this, 600);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(600);
            rotate.setInterpolator(new LinearInterpolator());
            imageView.startAnimation(rotate);
        }
    };

    public void setCallAlert() {
        if (GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getFromGoogle().equals(true)) {
            objPhone = GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getFormattedPhoneNumber();
            Log.e("GooglePlacesFinder", "call"+ objPhone);

        } else {
            objPhone = GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getPhone();
            Log.e("phoneeeeeee", "call" + "tel:" + "" + objPhone);
        }

        Log.e("setCallAlert","call"+objPhone);

        loadPhoneAlert(objPhone);

    }
    public void loadPhoneAlert(final String objPhone){
        final Dialog alertforcall = new Dialog(this);
        alertforcall.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertforcall.setCancelable(false);
        alertforcall.setCanceledOnTouchOutside(false);
        alertforcall.setCancelable(true);
        alertforcall.setContentView(R.layout.activity_alertforcall);
        alertforcall.show();
        alertforcall.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);
        TextView text = (TextView) alertforcall.findViewById(R.id.text_info);
        TextView textView1 = (TextView) alertforcall.findViewById(R.id.textView1);
        text.setText("Call to doctor");
        textView1.setText("Do You want make call to:");
        final TextView phoneText = (TextView) alertforcall.findViewById(R.id.editViewphonenumber);
        phoneText.setText(objPhone+"?");

        final Button no = (Button) alertforcall.findViewById(R.id.btn_no);
        no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertforcall.cancel();
            }
        });
        Button yes = (Button) alertforcall.findViewById(R.id.btn_yes);
        yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertforcall.cancel();
                if (objPhone != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + "" + objPhone));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    startActivity(callIntent);
                }
            }
        });
    }
    public void setMessageAlert() {

        if (GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getFromGoogle().equals(true)) {
            objmailOrWeb = GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getWebsite();
            Log.e("emailorWeb", "call" + "tel:" + "" + objmailOrWeb);
            if (objmailOrWeb != null) {
                Uri uri = Uri.parse(objmailOrWeb); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        } else {
           objmailOrWeb  = GooglePlacesFinder.getInstance().placesArray.get(selectedPosition).getEmail();
            Log.e("doctorEmail", "call" + objmailOrWeb);

            Log.e("responsealert", "call");
            final Dialog alertforcall = new Dialog(this);
            alertforcall.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertforcall.setCancelable(false);
            alertforcall.setCanceledOnTouchOutside(false);
            alertforcall.setCancelable(true);
            alertforcall.setContentView(R.layout.activity_alertforcall);
            alertforcall.show();

            alertforcall.getWindow().setBackgroundDrawableResource(R.drawable.layout_cornerbg);

            TextView text = (TextView) alertforcall.findViewById(R.id.text_info);

            TextView textView1 = (TextView) alertforcall.findViewById(R.id.textView1);

            text.setText("Mail to doctor");

            textView1.setText("Do You want send Mail to:");

            final TextView textEmail = (TextView) alertforcall.findViewById(R.id.editViewphonenumber);

            textEmail.setText(objmailOrWeb);

            final Button no = (Button) alertforcall.findViewById(R.id.btn_no);
            no.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.NO_KEY));
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertforcall.cancel();
                }
            });
            Button yes = (Button) alertforcall.findViewById(R.id.btn_yes);
            yes.setText(LanguageTextController.getInstance().currentLanguageDictionary.get(LanguagesKeys.YES_KEY));
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertforcall.cancel();
                    if (objmailOrWeb != null) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("plain/text");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{objmailOrWeb});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                        intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                        startActivity(Intent.createChooser(intent, ""));
                    }

                }


            });
        }
    }
}
