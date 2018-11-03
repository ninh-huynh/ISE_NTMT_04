package com.hcmus.dreamers.foodmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.google.gson.Gson;
import com.hcmus.dreamers.foodmap.AsyncTask.DoingTask;
import com.hcmus.dreamers.foodmap.AsyncTask.TaskCompleteCallBack;
import com.hcmus.dreamers.foodmap.AsyncTask.TaskRequest;
import com.hcmus.dreamers.foodmap.Model.Catalog;
import com.hcmus.dreamers.foodmap.Model.Dish;
import com.hcmus.dreamers.foodmap.Model.Owner;
import com.hcmus.dreamers.foodmap.common.GenerateRequest;
import com.hcmus.dreamers.foodmap.common.ResponseJSON;
import com.hcmus.dreamers.foodmap.define.ConstantCODE;
import com.hcmus.dreamers.foodmap.jsonapi.ParseJSON;

public class ManageDishActivity extends AppCompatActivity {


    EditText txtDishName;
    EditText txtDishCost;
    Spinner spnrDishType;
    GridView gridView;
    FloatingActionButton fab;
    Toolbar toolbar;

    Intent manageRest_manageDish;
    Bundle transferData = new Bundle();

    int rest_id;
    Dish dish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dish);

        takeReferenceFromResource();
        getTransferDataFromActivity();
        putDataToViews();

        gridView.setAdapter(new ImageAdapter(this));


        //Enable the Up button (Icon look like this: <- )
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // HANDLE ALL CLICK EVENT
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void putDataToViews() {
        txtDishName.setText(dish.getName());
        txtDishCost.setText(Integer.toString(dish.getPrice()));

    }

    private void getTransferDataFromActivity() {
        // Get the restaurant ID and dish Obj
        Gson gson = new Gson();
        manageRest_manageDish = getIntent();
        transferData = manageRest_manageDish.getExtras();

        //rest_id = transferData.getInt("restID");                TODO Remove this comment when the data is ready!
        String dishJSON = transferData.getString("dishJSON");
        dish = gson.fromJson(dishJSON, Dish.class);
    }

    private void takeReferenceFromResource() {
        txtDishName = (EditText) findViewById(R.id.txtDishName);
        txtDishCost = (EditText) findViewById(R.id.txtDishCost);
        spnrDishType = (Spinner) findViewById(R.id.dishType);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.edit_dish_toolbar);
        gridView  = (GridView) findViewById(R.id.gridview);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_delete:
                Toast.makeText(ManageDishActivity.this, "action delete selected",
                        Toast.LENGTH_LONG).show();
                return true;

            case R.id.action_done:
                Toast.makeText(ManageDishActivity.this, "action done selected",
                        Toast.LENGTH_LONG).show();

                if (checkInputValid() == false)
                {
                    Toast.makeText(ManageDishActivity.this, "There's something wrong",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
/*                updateInfomation(txtDishName.getText().toString(),
                        Integer.parseInt(txtDishCost.getText().toString()),
                        );*/

                //setResult(Activity.RESULT_OK, manageRest_manageDish);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkInputValid() {
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_item_menu,menu);
        return true;
    }

    public void updateInfomation(String name,
                                 int price,
                                 String urlImage,
                                 Catalog catalog,
                                 int rest_id){

        // Set the new Dish info
        final Dish newDish = new Dish(name,price,urlImage,catalog);

        TaskRequest updateDishInfoTask = new TaskRequest();

        // Implement call back
        updateDishInfoTask.setOnCompleteCallBack(new TaskCompleteCallBack() {
            @Override
            public void OnTaskComplete(Object response) {
                try
                {
                    ResponseJSON responseJSON =  ParseJSON
                            .parseFromAllResponse(response.toString());

                    // Pop-up the result message through Toast
                    if (ConstantCODE.SUCCESS == responseJSON.getCode()){
                        Toast.makeText(ManageDishActivity.this,
                                "Update successful!",
                                Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(ManageDishActivity.this,
                                responseJSON.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    Toast.makeText(ManageDishActivity.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        // Invoke task
        updateDishInfoTask.execute(new DoingTask(
                GenerateRequest
                        .updateDish(
                                rest_id,
                                newDish,
                                Owner.getInstance().getToken())));

        // Send the new Dish back to previous activity
        // ...
        Gson gson = new Gson();
        gson.toJson(newDish);
        transferData.putString("dishJSON",gson.toString());

    }
}