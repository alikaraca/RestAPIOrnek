package com.example.snow_.restapiornek1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText edit;
    TextView txt;
    Button btn;
    String baseUrl = "https://api.github.com/users/";  // Github API Url
    String url;
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit=(EditText) findViewById(R.id.editText);
        txt=(TextView) findViewById(R.id.textView);
        btn=(Button) findViewById(R.id.button);

        this.txt.setMovementMethod(new ScrollingMovementMethod());  //Textview scroll özelliği
        requestQueue = Volley.newRequestQueue(this);
    }
    private void clearRepoList() {
        //TextView içini silme
        this.txt.setText("");
    }

    private void addToRepoList(String repoName, String lastUpdated,String createdTime) {
       //Repo ekleme fonksiyonu
        String strRow = repoName + " / " + lastUpdated+" / "+createdTime;
        String currentText = txt.getText().toString();
        this.txt.setText(currentText + "\n\n" + strRow);
    }

    private void setRepoListText(String str) {
        this.txt.setText(str);
    }
    private void getRepoList(String username) {
        this.url = this.baseUrl + username + "/repos";

        // Dökümana buradan ulaşabilirsiniz: https://developer.android.com/training/volley/index.html
        JsonArrayRequest arrReq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Repository kontrolü yapıyoruz
                        if (response.length() > 0) {
                            // Kullanıcının Repository si var ise for döngüsüne giriyoruz.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    // burada da kaç tane repo var ise onları listeliyoruz.
                                    JSONObject jsonObj = response.getJSONObject(i);
                                    String repoName = jsonObj.get("name").toString();
                                    String lastUpdated = jsonObj.get("updated_at").toString();
                                    String createdTime=jsonObj.get("created_at").toString();
                                    addToRepoList(repoName, lastUpdated,createdTime);
                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    Log.e("Volley", "Invalid JSON Object.");
                                }

                            }
                        } else {
                            //Kullanıcının reposu yok ise else bloğu çalışır
                            setRepoListText("Repository Bulunamadı.");
                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // HTTP hatası verdiğinde bu blok çalışır.
                        setRepoListText("Rest Api çağırılırken hata oluştu.");
                        Log.e("Volley", error.toString());
                    }
                }
        );
        requestQueue.add(arrReq);
    }
    public void getReposClicked(View v) {
        //ilk başta temizliyoruz
        clearRepoList();
        //daha sonra edittexten gelen değere göre fonksiyonumuzu çalıştırıyoruz.
        getRepoList(edit.getText().toString());
    }
}
