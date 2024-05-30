package com.example.Helper;

import android.content.Context;
import android.widget.Toast;

import com.example.Model.Food;

import java.util.ArrayList;


public class ManagmentCart {
    private Context context;
    private TinyDB tinyDB;


    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public int getTotalNumberOfItems() {
        ArrayList<Food> listItem = getListCart();
        int totalItems = 0;
        for (Food food : listItem) {
            totalItems += food.getNumberInCart();
        }
        return totalItems;
    }
    public void resetCart(){
        ArrayList<Food> emptyList = new ArrayList<>();
        tinyDB.putListObject("CartList", emptyList);
    }

    public void insertFood(Food item) {
        ArrayList<Food> listpop = getListCart();
        boolean existAlready = false;

        // Tìm kiếm xem mặt hàng đã có sẵn trong giỏ hàng chưa
        for (int i = 0; i < listpop.size(); i++) {
            if (listpop.get(i).getTitle().equals(item.getTitle())) {
                existAlready = true;
                // Tăng số lượng của mặt hàng đã có sẵn
                int newQuantity = listpop.get(i).getNumberInCart() + item.getNumberInCart();
                listpop.get(i).setNumberInCart(newQuantity);
                break;
            }
        }

        // Nếu mặt hàng chưa có sẵn, thêm vào giỏ hàng
        if (!existAlready) {
            listpop.add(item);
        }

        // Lưu danh sách mới vào SharedPreferences
        tinyDB.putListObject("CartList", listpop);
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show();
    }


    public ArrayList<Food> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public Double getTotalFee() {
        ArrayList<Food> listItem = getListCart();
        double fee = 0;
        for (int i = 0; i < listItem.size(); i++) {
            fee = fee + (listItem.get(i).getPrice() * listItem.get(i).getNumberInCart());
        }
        return fee;
    }

    public void minusNumberItem(ArrayList<Food> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listItem.get(position).getNumberInCart() == 1) {
            listItem.remove(position);
        } else {
            listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() - 1);
        }
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }

    public void plusNumberItem(ArrayList<Food> listItem, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItem.get(position).setNumberInCart(listItem.get(position).getNumberInCart() + 1);
        tinyDB.putListObject("CartList", listItem);
        changeNumberItemsListener.change();
    }
}
