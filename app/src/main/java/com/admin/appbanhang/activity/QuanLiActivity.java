package com.admin.appbanhang.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.admin.appbanhang.R;
import com.admin.appbanhang.adapter.SanPhamMoiAdapter;
import com.admin.appbanhang.model.EventBus.XoaSua;
import com.admin.appbanhang.model.SanPhamMoi;
import com.admin.appbanhang.retrofit.ApiBanHang;
import com.admin.appbanhang.retrofit.RetrofitClient;
import com.admin.appbanhang.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import soup.neumorphism.NeumorphCardView;

public class QuanLiActivity extends AppCompatActivity {
    ImageView img_them;
    RecyclerView recyclerView;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> list;
    SanPhamMoiAdapter adapter;
    SanPhamMoi sanPhamSuaXoa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quan_li);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);
        initView();
        initControl();
        getSpMoi();
    }
    private void initControl() {
        img_them.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddSpActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()){
                                list = sanPhamMoiModel.getResult();
                                adapter = new SanPhamMoiAdapter(getApplicationContext(),list);
                                recyclerView.setAdapter(adapter);
                            }

                        },
                        throwable -> {
                            Toast.makeText(getApplicationContext(),"Không kết nối được với Server" + throwable.getMessage(),Toast.LENGTH_LONG).show();
                        }
                ));
    }

    private void initView() {
        img_them = findViewById(R.id.img_them);
        recyclerView = findViewById(R.id.recycleview_ql);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Sửa")){
            suaSanPham();
        }else if(item.getTitle().equals("Xóa")){
            xoasSanPham();
        }
        return super.onContextItemSelected(item);
    }

    private void xoasSanPham() {
        compositeDisposable.add(apiBanHang.xoaSanPham(sanPhamSuaXoa.getId())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            messageModel -> {
                if(messageModel.isSuccess()){
                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                    getSpMoi();
                }else{
                    Toast.makeText(getApplicationContext(),messageModel.getMessage(),Toast.LENGTH_LONG).show();
                }
            },
            throwable -> {
                Log.d("log",throwable.getMessage());
            }
        ));
    }

    private void suaSanPham() {
        Intent intent = new Intent(getApplicationContext(),AddSpActivity.class);
        intent.putExtra("sua",sanPhamSuaXoa);

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    public  void evenXoaSua(XoaSua event){
        if(event != null){
            sanPhamSuaXoa = event.getSanPhamMoi();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

    }
}