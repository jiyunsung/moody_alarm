package edu.dartmouth.cs.moodyalarm;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class SudokuActivity extends AppCompatActivity {

    private SudokuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        SudokuGenerator sudoku = new SudokuGenerator();
        GridView gridview = (GridView) findViewById(R.id.gridview);
        adapter = new SudokuAdapter(this, sudoku.generateGrid());
        //adapter = new SudokuAdapter(this, new int[81]);

        gridview.setAdapter(adapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(SudokuActivity.this, "kj", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class SudokuGenerator {

        private ArrayList<ArrayList<Integer>> Available = new ArrayList<ArrayList<Integer>>();

        private Random rand = new Random();

        private SudokuGenerator(){}

        public int[] generateGrid(){
            int[][] Sudoku = new int[9][9];
            int[] numbers = new int[81];

            int currentPos = 0;


            while( currentPos < 81 ){
                if( currentPos == 0 ){
                    clearGrid(Sudoku);
                }

                if( Available.get(currentPos).size() != 0 ){
                    int i = rand.nextInt(Available.get(currentPos).size());
                    int number = Available.get(currentPos).get(i);

                    if( !checkConflict(Sudoku, currentPos , number)){
                        int xPos = currentPos % 9;
                        int yPos = currentPos / 9;

                        Sudoku[xPos][yPos] = number;

                        Available.get(currentPos).remove(i);

                        currentPos++;
                    }else{
                        Available.get(currentPos).remove(i);
                    }

                }else{
                    for( int i = 1 ; i <= 9 ; i++ ){
                        Available.get(currentPos).add(i);
                    }
                    currentPos--;
                }
            }

            for (int x = 0; x < 9; x ++) {
                for (int y = 0; y < 9; y ++ ) {
                    numbers[x + y * 9] = Sudoku[x][y];
                }
            }
            return numbers;
        }

        public int[][] removeElements( int[][] Sudoku ){
            int i = 0;

            while( i < 3 ){
                int x = rand.nextInt(9);
                int y = rand.nextInt(9);

                if( Sudoku[x][y] != 0 ){
                    Sudoku[x][y] = 0;
                    i++;
                }
            }
            return Sudoku;

        }

        private void clearGrid(int [][] Sudoku){
            Available.clear();

            for( int y =  0; y < 9 ; y++ ){
                for( int x = 0 ; x < 9 ; x++ ){
                    Sudoku[x][y] = -1;
                }
            }

            for( int x = 0 ; x < 81 ; x++ ){
                Available.add(new ArrayList<Integer>());
                for( int i = 1 ; i <= 9 ; i++){
                    Available.get(x).add(i);
                }
            }
        }

        public boolean checkConflict( int[][] Sudoku , int currentPos , final int number){
            int xPos = currentPos % 9;
            int yPos = currentPos / 9;

            if( checkHorizontalConflict(Sudoku, xPos, yPos, number) || checkVerticalConflict(Sudoku, xPos, yPos, number) || checkRegionConflict(Sudoku, xPos, yPos, number) ){
                return true;
            }

            return false;
        }

        /**
         * Return true if there is a conflict
         * @param Sudoku
         * @param xPos
         * @param yPos
         * @param number
         * @return
         */
        private boolean checkHorizontalConflict( final int[][] Sudoku , final int xPos , final int yPos , final int number ){
            for( int x = xPos - 1; x >= 0 ; x-- ){
                if( number == Sudoku[x][yPos]){
                    return true;
                }
            }

            return false;
        }

        private boolean checkVerticalConflict( final int[][] Sudoku , final int xPos , final int yPos , final int number ){
            for( int y = yPos - 1; y >= 0 ; y-- ){
                if( number == Sudoku[xPos][y] ){
                    return true;
                }
            }

            return false;
        }

        private boolean checkRegionConflict( final int[][] Sudoku , final int xPos , final int yPos , final int number ){
            int xRegion = xPos / 3;
            int yRegion = yPos / 3;

            for( int x = xRegion * 3 ; x < xRegion * 3 + 3 ; x++ ){
                for( int y = yRegion * 3 ; y < yRegion * 3 + 3 ; y++ ){
                    if( ( x != xPos || y != yPos ) && number == Sudoku[x][y] ){
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public class SudokuAdapter extends BaseAdapter {
        private Context mContext;
        private Integer mThumbIds = R.drawable.cell_shape; // reference to cell
        private int[] Sudoku;
        private double probability = 0.15;
        private boolean[] blanks;
        private int[] inputs = new int[81];
        private HashMap<Integer, String> map = new HashMap<>();

        // initialize
        public SudokuAdapter(Context c, int[] numbers) {
            mContext = c;
            Sudoku = numbers;
            blanks = this.createBlanks();
        }

        public int getCount() {
            return 81;
        }

        public Object getItem(int position) {
            return this;
        }

        public long getItemId(int position) {
            return 0;
        }

        private boolean[] createBlanks() {
            Random rand = new Random();
            boolean[] blanks = new boolean[81];

            for (int i = 0; i < 81; i ++) {
                if (rand.nextDouble() < this.probability) {
                    blanks[i] = true;
                } else {
                    blanks[i] = false;
                }
            }
            return  blanks;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View view, ViewGroup parent) {

            if (this.blanks[position]) {
                final SquareEditText newView;
                if ( view == null ) {
                    newView = new SquareEditText( this.mContext );
                } else {
                    newView = (SquareEditText) view;
                }

                final int i = position;
                //Log.d("pos", Integer.toString(position));
                //Set the text:
                newView.setTextColor( Color.BLUE );  // android:textColor
                newView.setTextSize(25);  // android:textSize
                newView.setGravity( Gravity.CENTER );  // android:gravity
                newView.setInputType(InputType.TYPE_CLASS_NUMBER);
                newView.setSingleLine(true);
                newView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                newView.setBackground(null);

                //Set backgorund image to square cell:
                //newView.setBackgroundResource( this.mThumbIds );  // android:background

                newView.addTextChangedListener(new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        map.put(i, s.toString());

//                        new SudokuGenerator().checkConflict();
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });
                newView.setText(map.get(position));

                return newView;
            } else {
                SquareTextView newView;
                if ( view == null ) {
                    newView = new SquareTextView( this.mContext );
                } else {
                    newView = (SquareTextView) view;
                }

                //Log.d("pos", Integer.toString(position));
                //Set the text:
                newView.setText( Integer.toString(this.Sudoku[position]) );  // android:text
                newView.setTextColor( Color.BLACK );  // android:textColor
                newView.setTextSize(25);  // android:textSize
                newView.setTypeface( Typeface.DEFAULT_BOLD );  // android:textStyle
                newView.setGravity( Gravity.CENTER );  // android:gravity

                //Set backgorund image to square cell:
                //newView.setBackgroundResource( this.mThumbIds );  // android:background
                return newView;
            }
        }

        public boolean isCorrect() {
            for (int i = 0; i < 81; i ++) {
                if (this.blanks[i]) {
                    if (!map.containsKey(i) || (this.Sudoku[i] != Integer.parseInt(map.get(i)))){
                        return false;
                    }
                }
            }
            return true;
        }

    }

    // custom imageView class to make images square
    public class SquareTextView extends AppCompatTextView {
        public SquareTextView(Context context) {
            super(context);
        }

        public SquareTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }

    // custom imageView class to make images square
    public class SquareEditText extends AppCompatEditText {
        public SquareEditText(Context context) {
            super(context);
        }

        public SquareEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareEditText(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }

    public void onSudokuSubmit(View view){

        if (adapter.isCorrect()) {
            Toast.makeText(this, "correct!", Toast.LENGTH_SHORT).show();
            PopupActivity.alarm.stop_alert(getApplicationContext());
            finish();
        } else {
            Toast.makeText(this, "try again!", Toast.LENGTH_SHORT).show();
        }


    }
}
