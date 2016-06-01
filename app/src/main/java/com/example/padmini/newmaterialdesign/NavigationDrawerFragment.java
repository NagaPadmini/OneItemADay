package com.example.padmini.newmaterialdesign;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment implements CustomAdapter.ClickListener  {

    private RecyclerView recyclerView;
    public static final String PREF_FILE_NAME="testpref";
    public static final String KEY_USER_LEARNED_DRAWER="user_learned_drawer";
   private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private CustomAdapter adapter;
    private boolean mUserLearnedDrawer;//User is aware of Drawer existance or not
    private boolean mFromSavedInstanceState;//Fragment/Activity is started first time or coming from the Rotation
    private View containerView;
    private boolean isDrawerOpened=false;
    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Here Boolean.valueOf give the String Value we need where Boolean is the wrapper class
        mUserLearnedDrawer=Boolean.valueOf(readFromPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,"false"));//Here "false" means the User has never opened the drawer
        if(savedInstanceState!=null)//!=null means coming back from the Rotation if it is =null then Fragment is started very first time
        {
            mFromSavedInstanceState=true;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView= (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter=new CustomAdapter(getActivity(),getData());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
        Log.d("recyclerView", "setAdapter");
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return layout;
    }

    public static List<Information> getData()
    {
        List<Information> data=new ArrayList<>();
        int[] icons={R.drawable.ic_looks_one_white_24dp,R.drawable.ic_looks_two_white_24dp,R.drawable.ic_build_white_24dp,R.drawable.ic_pan_tool_white_24dp};
        String[] titles={"Joke","Word","Setting","Help"};
        //adding icons and titles values into the Information class
        for(int i=0;i<titles.length&&i<icons.length;i++)
        {
            Information current=new Information();
            current.iconId=icons[i];
            current.title=titles[i];
            //adding the current data into the List
            data.add(current);
        }
        return data;
    }


    public void setUp(int fragmentId,DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView=getActivity().findViewById(fragmentId);
        mDrawerLayout=drawerLayout;//mDrawerLayout to store the object that is passed from the MainActivity
        mDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            //onDrawerOpened indicates that the Drawer has just been viewed by the User
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer)//if the User has never seen the Drawer before
                {
                    mUserLearnedDrawer=true;//then this statement means the User has saw the Drawer just now
                    //and we have to save mUserLearnedDrawer=true in sharedPreferences
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER,mUserLearnedDrawer+"");
                }
                getActivity().invalidateOptionsMenu();//this means the Menu should be re-Drawn when Drawer is opened
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();//this means the Menu should be re-Drawn when Drawer is closed
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
               // super.onDrawerSlide(drawerView, slideOffset);
               if(slideOffset<0.6)
                {
                    toolbar.setAlpha(1-slideOffset);
                }
            }
        };
        if(!mUserLearnedDrawer&&!mFromSavedInstanceState)//if(the user has never seen the Drawer && very first time the fragment is started)
        {
            mDrawerLayout.openDrawer(containerView);//then Display the Drawer
            //containerView is a id-id/fragment_navigation_drawer, value passed in setUp method from MainActivity to NavigationDrawerFragment
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }



    public static void saveToPreferences(Context context,String preferenceName,String preferenceValue)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(preferenceName,preferenceValue);
        editor.apply();
    }
    public static String readFromPreferences(Context context,String preferenceName,String defaultValue)
    {
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREF_FILE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName,defaultValue);
    }


    @Override
    public void itemClicked(View view, int recyclerItemPosition) {
        if(recyclerItemPosition==0) {
            Intent intent=new Intent(getActivity(),OneItemADay.class);
            Bundle bundle=new Bundle();
            bundle.putInt("type",1);
            intent.putExtras(bundle);
            startActivity(intent);
            Toast.makeText(getActivity(),"you selected Joke",Toast.LENGTH_LONG).show();

        }
        else if(recyclerItemPosition==1){
            Intent intent=new Intent(getActivity(),OneItemADay.class);
            Bundle bundle=new Bundle();
            bundle.putInt("type",2);
            intent.putExtras(bundle);
            startActivity(intent);
            Toast.makeText(getActivity(),"you selected Word",Toast.LENGTH_LONG).show();
        }
        else if(recyclerItemPosition==2){
            Intent intent=new Intent(getActivity(),Setting.class);
            startActivity(intent);
            Toast.makeText(getActivity(),"you selected Settings",Toast.LENGTH_LONG).show();
        }
        else if(recyclerItemPosition==3)
        {
            Intent intent=new Intent(getActivity(),Help.class);
            startActivity(intent);
            Toast.makeText(getActivity(),"You selected Help",Toast.LENGTH_LONG).show();
        }
    }
}
