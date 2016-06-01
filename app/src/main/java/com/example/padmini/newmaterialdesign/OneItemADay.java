package com.example.padmini.newmaterialdesign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class OneItemADay extends AppCompatActivity {

    private Toolbar toolbar;
    private static ViewPager mPager;
    private SlidingTabLayout mTabs;



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onResumeFragments() {
        try {
            super.onResumeFragments();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mPager = (ViewPager) findViewById(R.id.pager);
        if (mPager != null) {
            mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        }
        //mPager.setCurrentItem(2, false);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mPager);


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        //drawerFragment.setUp() is used to pass some values from mainActivity to NavigationDrawer
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPager.setCurrentItem(0);
    }


    @Override
   public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.navigate) {
            Intent intentNavigate=new Intent(OneItemADay.this,Setting.class);
            startActivity(intentNavigate);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabs;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
            MyFragment myFragment = MyFragment.getInstance(position);

            return myFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    public static class MyFragment extends Fragment {
        private TextView textView;
        private View mLayout;
        private String text=null;

        public static MyFragment getInstance(int position) {
            MyFragment myFragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            mLayout = inflater.inflate(R.layout.fragment_my, container, false);
            return mLayout;

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            textView = (TextView) mLayout.findViewById(R.id.position);
            Bundle recyclerItemSelected = getActivity().getIntent().getExtras();
            Bundle bundle = getArguments();

            int position = bundle.getInt("position");//This Bundle is from MyFragment
            if(savedInstanceState!=null)
            {
                String savedTextValue=savedInstanceState.getString("SavedTextValue");
                textView.setText(savedTextValue);
                registerForContextMenu(textView);
                return;
            }
            Bundle readFromNotification=getActivity().getIntent().getExtras();
           if(readFromNotification!=null)
            {
                String textNotification=readFromNotification.getString("Passing Text from ServiceNotification");
                if( textNotification != null && !textNotification.isEmpty() && position ==0 ) {
                    textView.setText(textNotification);
                   // registerForContextMenu(textView);
                   // mPager.setCurrentItem(0,false);
                    return;
                }
               // mPager.setCurrentItem(0,false);
            }

            if( recyclerItemSelected==null) {
                if( position == 0) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("saving_the_Joke", Context.MODE_PRIVATE);
                    String readJoke = preferences.getString("Joke_Value", " ");
                    textView.setText(readJoke);
                    registerForContextMenu(textView);
                }else if( position ==1) {
                    SharedPreferences preferences=getActivity().getSharedPreferences("saving_the_Word", Context.MODE_PRIVATE);
                    String readWord=preferences.getString("Word_Value"," ");
                    textView.setText(readWord);
                    registerForContextMenu(textView);
                }
                return;
            }

            if (recyclerItemSelected != null && bundle != null) {

                int type = recyclerItemSelected.getInt("type");//This Bundle is from NavigationDrawerFragment

                if (type != 1 && position == 0) {
                    SharedPreferences preferences=getActivity().getSharedPreferences("saving_the_Joke", Context.MODE_PRIVATE);
                    String readJoke=preferences.getString("Joke_Value"," ");
                    textView.setText(readJoke);
                    registerForContextMenu(textView);
                    return;
                }
                else if(type!=2 && position == 1){
                    SharedPreferences preferences=getActivity().getSharedPreferences("saving_the_Word", Context.MODE_PRIVATE);
                    String readWord=preferences.getString("Word_Value"," ");
                    textView.setText(readWord);
                    registerForContextMenu(textView);
                    return;

                }
            }

           registerForContextMenu(textView);



            if (recyclerItemSelected != null && bundle != null) {

                int type = recyclerItemSelected.getInt("type");//This Bundle is from NavigationDrawerFragment
               // int position = bundle.getInt("position");//This Bundle is from MyFragment

                if (type == 1 && position == 0)

                {

                    JokeTask task = new JokeTask();
                    task.execute("http://api.icndb.com/jokes/random");

                    //val ---   you can get it from DB
                    // Val --   you can get it from Restful url
                    //val --   you can compute by calling some complex api  val =   someLibraryClass.getPiValue
                    //val --   you can hardcode

                    // textView.setText(val);
                } else if (type == 2 && position == 1) {
                    WordTask task = new WordTask();
                   // task.execute("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url=%27http://www.santabanta.com/jokes/%27%20and%20xpath=%22//table//tr/td%22&format=json&env=store://datatables.org/alltableswithkeys");
                    task.execute("https://wordsapiv1.p.mashape.com/words/?random=true&mashape-key=qdkkUvzbMjmshGkXsDb40hYVo2Vup13f35Rjsn5dqLAsxk1LJb&hasDetails=definitions");
                }
            }

        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            String savedTextValue=textView.getText().toString();
            outState.putString("SavedTextValue",savedTextValue);
        }

        class JokeTask extends AsyncTask<String, Void, String> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            @Override
            protected String doInBackground(String... params) {
                String text = null;
                try {
                    URL u = new URL(params[0]);

                    conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("GET");

                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();

                    Log.e("jsonmsg for Joke", finalJson);

                    JSONObject parentObject = new JSONObject(finalJson);
                    JSONObject obj = (JSONObject) parentObject.get("value");
                    text = (String) obj.get("joke");


                    if( text != null) {
                        text = text.replaceAll("&quot;", "\"");
                    }


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                //Here shared Preferences is used to save the Previous Joke to display when coming back from word tab
                SharedPreferences preferences = getActivity().getSharedPreferences("saving_the_Joke", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Joke_Value", s);
                editor.commit();
                textView.setText(s);
            }
        }

        class WordTask extends AsyncTask<String, Void, String> {
            HttpURLConnection conn = null;
            BufferedReader reader = null;

            @Override
            protected String doInBackground(String... params) {
                String text = null;
                try {
                    URL u = new URL(params[0]);

                    conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestMethod("GET");

                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    String finalJson = buffer.toString();

                    Log.e("jsonmsg for word", finalJson);


                    JSONObject parentObject=new JSONObject(finalJson);

                    String getWord= (String) parentObject.get("word");
                    String definition=null;
                    JSONArray arr= (JSONArray) parentObject.get("results");
                   for (int i = 0; i < arr.length(); i++) {
                       JSONObject jsonobject = (JSONObject) arr.getJSONObject(i);
                      definition=jsonobject.getString("definition");
                   }
                    text=getWord+"="+definition;


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return text;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                SharedPreferences preferences = getActivity().getSharedPreferences("saving_the_Word", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Word_Value", s);
                editor.commit();
                textView.setText(s);
                mPager.setCurrentItem(1,false);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            getActivity().getMenuInflater().inflate(R.menu.menu_my_fragment, menu);
        }

        @Override
        public boolean onContextItemSelected(MenuItem item) {

            if(! getUserVisibleHint()) {
                return  false;
            }

           // AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Bundle bundle=getArguments();
            Intent intent=null, chooser = null;

            int position = bundle.getInt("position");

            switch (item.getItemId())
            {
                case R.id.action_save:
                    if(position == 0) {

                        text = textView.getText().toString();
                        if(text==null||text.isEmpty())
                        {
                            Toast.makeText(getActivity(),"No Joke to save",Toast.LENGTH_LONG).show();
                            return true;
                        }


                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
                        alert.setMessage("Enter Your File Name"); //Message here

                        // Set an EditText view to get user input
                        final EditText input = new EditText(getActivity());
                        alert.setView(input);

                        if(input.getEditableText()!=null)
                        {
                            SharedPreferences preferences=getActivity().getSharedPreferences("saving_dataOn_editText", Context.MODE_PRIVATE);
                            String readEditData=preferences.getString("EditText_Value"," ");
                            input.setText(readEditData);
                        }

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                String srt = input.getEditableText().toString();
                                if (srt != null) {
                                    SharedPreferences preferences = getActivity().getSharedPreferences("saving_dataOn_editText", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("EditText_Value", srt);
                                    editor.commit();
                                }

                                text = text + "\n\n ";//space will be created after every joke/word/text
                                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                                if (srt == null || srt.trim().length() == 0) {
                                    Toast.makeText(getActivity(), "Please enter the file name", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                File myFile = new File(folder, srt + ".txt");

                                FileWriter fw = null;
                                try {
                                    fw = new FileWriter(myFile, true);
                                    fw.append(text);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (fw != null)
                                        try {
                                            fw.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                }

                                Toast.makeText(getActivity(), text + "saved successfully" + myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            } // End of onClick(DialogInterface dialog, int whichButton)
                        }); //End of alert.setPositiveButton
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.cancel();
                            }
                        }); //End of alert.setNegativeButton
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    }
                    else if(position == 1)
                    {
                         text = textView.getText().toString();
                        if(text == null || text.isEmpty())
                        {
                            Toast.makeText(getActivity(),"No word to save",Toast.LENGTH_LONG).show();
                            return true;
                        }
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Alert Dialog With EditText"); //Set Alert dialog title here
                        alert.setMessage("Enter Your File Name"); //Message here

                        // Set an EditText view to get user input
                        final EditText input = new EditText(getActivity());
                        alert.setView(input);

                        if(input.getEditableText()!=null)
                        {
                            SharedPreferences preferences=getActivity().getSharedPreferences("saving_dataOn_editText", Context.MODE_PRIVATE);
                            String readEditData=preferences.getString("EditText_Value"," ");
                            input.setText(readEditData);
                        }

                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                String srt = input.getEditableText().toString();


                                if (srt != null) {
                                    SharedPreferences preferences = getActivity().getSharedPreferences("saving_dataOn_editText", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("EditText_Value", srt);
                                    editor.commit();
                                }

                                text = text + "\n\n ";//space will be created after every joke/word/text
                                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                                if (srt == null || srt.trim().length() == 0) {
                                    Toast.makeText(getActivity(), "Please enter the file name", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                File myFile = new File(folder, srt + ".txt");

                                FileWriter fw = null;
                                try {
                                    fw = new FileWriter(myFile, true);
                                    fw.append(text);


                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    if (fw != null)
                                        try {
                                            fw.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                }

                                Toast.makeText(getActivity(), text + "saved successfully" + myFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                            } // End of onClick(DialogInterface dialog, int whichButton)
                        }); //End of alert.setPositiveButton
                        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.cancel();
                            }
                        }); //End of alert.setNegativeButton
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                    }
                    break;
                case R.id.action_share:
                     text= textView.getText().toString();
                    if(text == null || text.isEmpty())
                    {
                        Toast.makeText(getActivity(),"No Text to share",Toast.LENGTH_LONG).show();
                        return true;
                    }
                    intent=new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    // Add data to the intent, the receiving app will decide what to do with it.
                    intent.putExtra(Intent.EXTRA_SUBJECT,"SHARE");
                    intent.putExtra(Intent.EXTRA_TEXT,textView.getText());
                    chooser=Intent.createChooser(intent,"How do I share text");
                    startActivity(chooser);
                    break;
            }

            return super.onContextItemSelected(item);

        }
    }
}