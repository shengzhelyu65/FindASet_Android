package be.kuleuven.findaset.activities;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import be.kuleuven.findaset.R;
import be.kuleuven.findaset.base.RVAdapterHighScore;
import be.kuleuven.findaset.base.RVAdapterNormal;

public class LeaderBoardActivity extends AppCompatActivity {
    private TextView tvAll;
    private TextView tvTen;
    private TextView tvHighScore;
    private ConstraintLayout tabModes;
    private RecyclerView recyclerView;
    private RVAdapterNormal allAdapter;
    private RVAdapterNormal tenAdapter;
    private RVAdapterHighScore highScoreAdapter;
    private String[] namesAll;
    private String[] timesAll;
    private String[] rankingsAll;
    private String[] namesTen;
    private String[] timesTen;
    private String[] rankingsTen;
    private String[] modes;
    private String[] scores;
    private String [] dates;
    private String [] rankingsHighScore;
    private RequestQueue requestQueue;
    private String baseURL = "https://studev.groept.be/api/a21pt113/";
    private String [][] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        tvAll = findViewById(R.id.tvAll);
        tvTen = findViewById(R.id.tvTen);
        tvHighScore = findViewById(R.id.tvHighScore);
        tabModes = findViewById(R.id.tabModes);
        recyclerView = findViewById(R.id.rvBoard);
        requestQueue = Volley.newRequestQueue( this );

        getRankingsAll();
        getRankingsTen();
        recyclerView = findViewById(R.id.rvBoard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // TODO - bind data from credentials to ranking
        data = new String[2][2];
        try {
            readCredentials();
        } catch (IOException e) {
            e.printStackTrace();
        }
        getRankingsAll();

        modes = new String[]{"Find All", "Find Ten"};
        scores = new String[]{"1'30", "0'30"};
        dates = new String[]{"22 May 2022", "21 May 2022"};
        rankingsHighScore = new String[]{"1", "2"};
        highScoreAdapter = new RVAdapterHighScore(modes, scores, dates, rankingsHighScore);
    }

    private void readCredentials() throws IOException {
        String s = getFilesDir() + "/" + "credentials";
        //https://stackoverflow.com/questions/33638765/how-to-read-json-data-from-txt-file-in-java
        BufferedReader reader = new BufferedReader(new FileReader(s));
        String json = "";
        json = getJSONString(reader);
        try {
            JSONObject object = new JSONObject(json);

            JSONArray session = object.getJSONArray("session");
            String username = session.getJSONObject(0).getString("username");
            if(username.equals(" ")){


            }
            else {
                getRankingsLoggedUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRankingsLoggedUser() {
        // TODO - query to get only user's high score info from DB
    }

    private String getJSONString(BufferedReader reader) throws IOException {
        String json = "";
        try {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            json = sb.toString();
        } finally {
            reader.close();
        }

        return json;
    }

    private void getRankingsAll() {
        String allSetsUrl = baseURL + "boardFindAll";
        JsonArrayRequest rankingRequestAll = new JsonArrayRequest(Request.Method.GET, allSetsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                namesAll = new String[response.length()];
                timesAll = new String[response.length()];
                rankingsAll = new String[response.length()];
                for (int i=0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        namesAll[i] = (String) o.get("username");
                        timesAll[i] = (String) o.get("allSetsRecord");
                        rankingsAll[i] = String.valueOf(i+1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                allAdapter = new RVAdapterNormal(namesAll, timesAll, rankingsAll);
                recyclerView.setAdapter(allAdapter);
                rvSetAnimation();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Database", "onErrorResponse: " + error);
            }
        });
        requestQueue.add(rankingRequestAll);
    }

    private void getRankingsTen() {
        String tenSetsUrl = baseURL + "boardFindTen";
        JsonArrayRequest rankingRequestTen = new JsonArrayRequest(Request.Method.GET, tenSetsUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                namesTen = new String[response.length()];
                timesTen = new String[response.length()];
                rankingsTen = new String[response.length()];
                for (int i=0; i<response.length(); i++) {
                    JSONObject o = null;
                    try {
                        o = response.getJSONObject(i);
                        namesTen[i] = (String) o.get("username");
                        timesTen[i] = (String) o.get("tenSetsRecord");
                        rankingsTen[i] = String.valueOf(i+1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tenAdapter = new RVAdapterNormal(namesTen, timesTen, rankingsTen);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Database", "onErrorResponse: " + error);
            }
        });
        requestQueue.add(rankingRequestTen);
    }

    public void onClick_Back(View caller) {
        finish();
    }

    public void onClick_All(View caller) {
        setConstraintGravity(R.id.tvAll);

        recyclerView.setAdapter(allAdapter);
        rvSetAnimation();
    }

    public void onClick_Ten(View caller) {
        setConstraintGravity(R.id.tvTen);

        recyclerView.setAdapter(tenAdapter);
        rvSetAnimation();
    }

    public void onClick_HighScore(View caller) {
        setConstraintGravity(R.id.tvHighScore);

        recyclerView.setAdapter(highScoreAdapter);
        rvSetAnimation();
    }

    private void rvSetAnimation() {
        LayoutAnimationController anim = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(anim);
    }

    private void setConstraintGravity(int viewId) {
        tvAll.setTextColor(getColor(R.color.textColorSecondary));
        tvTen.setTextColor(getColor(R.color.textColorSecondary));
        tvHighScore.setTextColor(getColor(R.color.textColorSecondary));

        ConstraintSet tabCons = new ConstraintSet();
        tabCons.clone(tabModes);
        TextView chosen = (TextView) findViewById(viewId);
        chosen.setTextColor(getColor(R.color.colorPrimary));
        tabCons.connect(
                R.id.tab,
                ConstraintSet.START,
                viewId,
                ConstraintSet.START,
                0
        );
        tabCons.connect(
                R.id.tab,
                ConstraintSet.END,
                viewId,
                ConstraintSet.END,
                0
        );
        tabCons.connect(
                R.id.tab,
                ConstraintSet.TOP,
                viewId,
                ConstraintSet.BOTTOM,
                0
        );
        animate(tabModes, tabCons);
    }

    private void animate(ConstraintLayout cl, ConstraintSet set) {
        TransitionManager.beginDelayedTransition(cl, new MyTransition());
        set.applyTo(cl);
    }

    static public class MyTransition extends TransitionSet {
        {
            setDuration(500);
            setOrdering(ORDERING_SEQUENTIAL);
            addTransition(new TransitionSet() {
                {
                    addTransition(new Fade(Fade.OUT));
                    addTransition(new ChangeBounds());
                    addTransition(new Fade(Fade.IN));
                }
            });
        }
    }
}