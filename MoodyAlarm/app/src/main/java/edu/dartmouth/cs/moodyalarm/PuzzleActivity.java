package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class PuzzleActivity extends AppCompatActivity {

    private PuzzleAdapter adapter;
    private ArrayList<Integer> clicked;
    private GridView gridview;
    private double difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        clicked = new ArrayList<Integer>();

        gridview = (GridView) findViewById(R.id.gridview);
        adapter = new PuzzleAdapter(this);
        gridview.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences(SnoozeSettings.PREFS_NAME, 0);
        difficulty = prefs.getInt(SnoozeSettings.PUZZLE_DIFF, 30) / 100.0;

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                if (clicked.contains(position)) {
                    clicked.remove(clicked.indexOf(position));
                    int background = adapter.puzzle.convertTernary(adapter.puzzle.grid[position])[2];
                    if (background == 0)
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_black);
                    else if (background == 1)
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_gray);
                    else
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_white);
                } else if (clicked.size() < 3) {
                    clicked.add(position);
                    int background = adapter.puzzle.convertTernary(adapter.puzzle.grid[position])[2];
                    if (background == 0)
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_black_highlighted);
                    else if (background == 1)
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_gray_highlighted);
                    else
                        parent.getChildAt(position).setBackgroundResource(R.drawable.cell_white_highlighted);
                } else {
                    Toast.makeText(PuzzleActivity.this, "only select up to three!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(PuzzleActivity.this, "Solve the puzzle please!", Toast.LENGTH_SHORT).show();
    }

    public class PuzzleAdapter extends BaseAdapter {
        private Context mContext;
        private PuzzleGenerator puzzle = new PuzzleGenerator().puzzleGenerator();
        private int[] shapes = puzzle.grid;

        public PuzzleAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return 9;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            SquareImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new SquareImageView(mContext);
                imageView.setScaleType(SquareImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(100, 100, 100, 100);

                int[] factors = puzzle.convertTernary(shapes[position]);

                if (factors[0] == 0){
                    ShapeDrawable circle= new ShapeDrawable( new OvalShape());
                    circle.setIntrinsicHeight( 120 );
                    circle.setIntrinsicWidth( 120);
                    circle.setBounds(new Rect(60, 60, 60, 60));
                    circle.getPaint().setColor(Color.parseColor(puzzle.color[factors[1]]));//you can give any color here
                    imageView.setImageDrawable(circle);
                } else if (factors[0] == 1) {

                    final ContextThemeWrapper wrapper;
                    if (factors[1] == 0)
                        wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.DefaultScene);
                    else if (factors[1] == 1)
                        wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.UpdatedScene);
                    else
                        wrapper = new ContextThemeWrapper(getApplicationContext(), R.style.UpdatedScene2);

                    final Drawable drawable = VectorDrawableCompat.create(getResources(), R.drawable.triangle, wrapper.getTheme());
                    imageView.setImageDrawable(drawable);

                } else {
                    Drawable square = getApplicationContext().getResources().getDrawable(R.drawable.square);
                    square.setColorFilter(new PorterDuffColorFilter(Color.parseColor(puzzle.color[factors[1]]), PorterDuff.Mode.MULTIPLY));
                    imageView.setImageDrawable(square);
                }

                if (factors[2] == 0) {
                    imageView.setBackgroundResource(R.drawable.cell_black);
                } else if (factors[2] == 1) {
                    imageView.setBackgroundResource(R.drawable.cell_gray);
                } else
                    imageView.setBackgroundResource(R.drawable.cell_white);

            } else {
                imageView = (SquareImageView) convertView;
            }
            return imageView;
        }
    }

    // custom imageView class to make images square
    public class SquareImageView extends AppCompatImageView {
        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }

    public class PuzzleGenerator {

        private ArrayList<Integer> Available = new ArrayList<Integer>();
        public String[] shape = {"circle", "triangle", "square"};
        public String[] color = {"#FF0000", "#FFFF00", "#0000FF"};
        public String[] background_color = {"#000000", "#808080", "#FFFFFF"};
        private int[] grid;
        private int number_of_sets = 0;
        private ArrayList<Integer> found = new ArrayList<Integer>();

        private PuzzleGenerator puzzleGenerator() {
            this.grid = generateGrid();
            return this;
        }

        public int[] generateGrid() {

            Available.clear();
            for (int i = 0; i < 27; i++) {
                Available.add(i);
            }

            grid = new int[9];
            Random rand = new Random();

            for (int i = 0; i < 9; i ++) {
                int chosen = rand.nextInt(Available.size());
                grid[i] = Available.get(chosen);
                Available.remove(chosen);

            }

            for (int a = 0; a < 7; a++) {
                for (int b = a + 1; b < 8; b++) {
                    for (int c = b + 1; c < 9; c++) {
                        if (isValid(this.grid[a], this.grid[b], this.grid[c])) {
                            number_of_sets += 1;
                        }
                    }
                }
            }
            return grid;
        }

        public boolean isValid(int x, int y, int z){
            int[] x_3 = convertTernary(x);
            int[] y_3 = convertTernary(y);
            int[] z_3 = convertTernary(z);

            if (((x_3[0] + y_3[0] + z_3[0]) % 3 == 0) && ((x_3[1] + y_3[1] + z_3[1]) % 3 == 0) && ((x_3[2] + y_3[2] + z_3[2]) % 3 == 0))
                return true;
            else
                return false;
        }

        private int[] convertTernary(int x){
            int[] ternary = new int[3];
            ternary[0] = x / 9;
            ternary[1] = (x % 9) / 3;
            ternary[2] = (x % 9) % 3;
            return ternary;
        }

        // takes in the positions of the three shapes and saves it to the found_set as unique integers
        private int get_unique_key(int a, int b, int c) {
            int key;
            if (a < b) {
                if (b < c) {
                    key = (a * 9 ^ 2 + b * 9 + c);
                } else if (c < a){
                    key = (c * 9 ^ 2 + a * 9 + b);
                } else {
                    key = (a * 9 ^ 2 + b * 9 + c);
                }
            } else {
                if (a < c) {
                    key = (b * 9 ^ 2 + a * 9 + c);
                } else if (c < b) {
                    key = (c * 9 ^ 2 + b * 9 + a);
                } else {
                    key = (b * 9 ^ 2 + c * 9 + a);
                }
            }
            return key;
        }
    }
    public void onPuzzleSubmit(View view) {
        if (clicked.size() != 3) {
            Toast.makeText(PuzzleActivity.this, "select exactly three blocks!", Toast.LENGTH_SHORT).show();
        } else {
            int key = adapter.puzzle.get_unique_key(clicked.get(0), clicked.get(1), clicked.get(2));
            if (adapter.puzzle.found.contains(key)) {
                Toast.makeText(PuzzleActivity.this, "You found this already", Toast.LENGTH_SHORT).show();
            } else if (adapter.puzzle.isValid(adapter.puzzle.grid[clicked.get(0)], adapter.puzzle.grid[clicked.get(1)], adapter.puzzle.grid[clicked.get(2)])) {
                adapter.puzzle.found.add(key);
                Toast.makeText(PuzzleActivity.this, "correct!", Toast.LENGTH_SHORT).show();

                if (adapter.puzzle.found.size() / (double) adapter.puzzle.number_of_sets > difficulty) {
                    PopupActivity.alarm.stop_alert(this);
                    finish();
                }
            } else {
                Toast.makeText(PuzzleActivity.this, "try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onResubmit(View view) {
        if (adapter.puzzle.number_of_sets == 0) {
            Toast.makeText(PuzzleActivity.this, "correct!", Toast.LENGTH_SHORT).show();
            PopupActivity.alarm.stop_alert(this);
            finish();
        } else {
            Toast.makeText(PuzzleActivity.this, "try again!", Toast.LENGTH_SHORT).show();
            clicked.clear();
            adapter = new PuzzleAdapter(this);
            gridview.setAdapter(adapter);
        }
    }
}
