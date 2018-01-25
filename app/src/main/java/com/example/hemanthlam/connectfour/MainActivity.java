package com.example.hemanthlam.connectfour;

import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Where the fun begins
        generateGrid();
    }

    public void generateGrid() {
        // Get main activity window relative layout. We need this when we attach the the Linear Layouts to i
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.mainActivityRL1);

        // https://stackoverflow.com/questions/4743116/get-screen-width-and-height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int numColumns = 7;
        int numRows = 6;
        Button buttonArr[] = new Button[numRows];
        int columnWidth;
        int height;

        // https://stackoverflow.com/questions/3663665/how-can-i-get-the-current-screen-orientation
        // Change heignt and width depending on screen orientation
        if (layout.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            columnWidth=1+(displayMetrics.widthPixels / numColumns);
            height = displayMetrics.widthPixels;
        }
        else {
            columnWidth=1+(displayMetrics.widthPixels / numColumns);

            // HeightPixels doesn't actually get the right window size when the phone is in landscape mode.
            // I presume it has something to do with screen realistate used by the header of the app
            // I am not sure how difficult it will be to deal with that later
            height = displayMetrics.heightPixels - 150;
        }

        // Create the linear layout of columns
        for (int i = 0; i < numColumns; ++i) {
            // Referenced/Looked at: https://stackoverflow.com/questions/7241145/creating-linearlayout-in-java-elements-are-not-shown
            // Referenced/Looked at: https://stackoverflow.com/questions/10159372/android-view-layout-width-how-to-change-programmatically
            // Internet also advised to use setMargins
            // The code block below creates the lienar layout
            LinearLayout linLayout = new LinearLayout(layout.getContext()/*getApplicationContext()*/);
            linLayout.setId(i);

            // To assign it parameters, you need to create a layoutParameters object, configure it, and assign it to the object you want to give the parameters to
            LinearLayout.LayoutParams layoutParamaters = new LinearLayout.LayoutParams(columnWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            linLayout.setOrientation(LinearLayout.VERTICAL);
            linLayout.setLayoutParams(layoutParamaters);

            // Add Linear Layout to main window
            layout.addView(linLayout);

            // Add some offset so the layouts don't end up stacked on top of each other
            // It seems that when I didn't do this, android would put the LinearLayouts directly on top of each other
            linLayout.setX(i*columnWidth);

            // For each liner layout (which represents a column), we now need rows
            // This loop takes care of that
            for (int j = 0; j < numRows; j++)
            {
                // I am going to use ImageButtons for the grid
                // Regular buttons had some padding that I didn't like
                ImageButton imageView = new ImageButton(linLayout.getContext());
                //imageView.setImageURI(Uri.parse("something!")); <-- The correct URL of an image would go here

                // Referenced/Looked at: https://www.android-examples.com/set-onclicklistener-on-imageview-in-android-example/
                // I wanted to add OnClick functionality so the image buttons do something when clicked (there is no clicking animation for ImageButtons)
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setBackgroundColor(Color.rgb(120, 250, 90));
                    }
                });

                // Referenced/Looked at: https://stackoverflow.com/questions/33336058/how-to-add-buttons-below-each-other-dynamically
                // More layout paremeter configuration
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height / numRows);
                imageView.setLayoutParams(params);
                imageView.setBackgroundColor(Color.rgb((10*i)+(10*j)+(10*j*i), (20*i)+(20*j)+(20*j*i), (40*i)+(40*j)+(40*j*i)));
                imageView.setId((i*10)+j);

                // Add the row to the column (add the ImageButton to the LinearLayout)
                linLayout.addView(imageView);
            }
        }
    }
}