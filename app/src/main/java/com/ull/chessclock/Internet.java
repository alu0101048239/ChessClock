package com.ull.chessclock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Internet extends SuperActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_internet);
    modelo.SetInternet(true);
    ReturnData();

  }

  public void ReturnData() {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("Modelo",  modelo);
    setResult(Activity.RESULT_OK, returnIntent);
  }
}