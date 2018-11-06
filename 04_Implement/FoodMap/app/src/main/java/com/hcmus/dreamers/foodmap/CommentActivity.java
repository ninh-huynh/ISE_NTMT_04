package com.hcmus.dreamers.foodmap;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hcmus.dreamers.foodmap.AsyncTask.TaskCompleteCallBack;
import com.hcmus.dreamers.foodmap.Model.Comment;
import com.hcmus.dreamers.foodmap.Model.Guest;
import com.hcmus.dreamers.foodmap.Model.Owner;
import com.hcmus.dreamers.foodmap.adapter.CommentListAdapter;
import com.hcmus.dreamers.foodmap.common.FoodMapApiManager;
import com.hcmus.dreamers.foodmap.common.FoodMapManager;
import com.hcmus.dreamers.foodmap.define.ConstantCODE;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView lstComment;
    EditText edtComment;
    ImageView igvComment;
    int id_rest;

    List<Comment> comments;
    CommentListAdapter commentListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_comment);

        comments = new ArrayList<Comment>();
        commentListAdapter = new CommentListAdapter(CommentActivity.this,R.layout.item_comment_list,comments);
        //commentListAdapter.setOnClickListener(this);
        loadDataRecyclerView();

        lstComment = (RecyclerView)findViewById(R.id.lstComment);
        lstComment.setAdapter(commentListAdapter);

        edtComment = (EditText)findViewById(R.id.edtComment);
        igvComment = (ImageView)findViewById(R.id.igvComment);

        igvComment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.igvComment){
            if (!FoodMapApiManager.isGuestLogin() && !FoodMapApiManager.isLogin()){
                Toast.makeText(CommentActivity.this,"Bạn phải đăng nhập trước", Toast.LENGTH_LONG).show();
            }
            else{
                if (edtComment.getText().equals("")){
                    final Comment comment = new Comment();
                    comment.setComment(edtComment.getText().toString());
                    String token = null;

                    if (FoodMapApiManager.isGuestLogin()){
                        comment.setEmailGuest(Guest.getInstance().getEmail());
                    }
                    else if (FoodMapApiManager.isLogin()){
                        comment.setEmailOwner(Owner.getInstance().getEmail());
                        token = Owner.getInstance().getToken();
                    }


                    FoodMapApiManager.addComment(id_rest, comment, token, new TaskCompleteCallBack() {
                        @Override
                        public void OnTaskComplete(Object response) {
                            int code = (int)response;
                            if (code == FoodMapApiManager.SUCCESS){
                                comments.add(comment);
                                commentListAdapter.notifyDataSetChanged();
                            }
                            else if (code == ConstantCODE.NOTINTERNET){
                                Toast.makeText(CommentActivity.this,"Kiểm tra kết nối internet", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(CommentActivity.this,"Không thể bình luận", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(CommentActivity.this,"Không để trống nội dung bình luận", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void loadDataRecyclerView(){
        Intent intent = getIntent();
        if (intent != null){
            int id_rest = intent.getIntExtra("id_rest",-1);
            if (id_rest != -1){
                this.id_rest = id_rest;
                comments = FoodMapManager.getComment(CommentActivity.this,id_rest);
            }
        }
    }
}
