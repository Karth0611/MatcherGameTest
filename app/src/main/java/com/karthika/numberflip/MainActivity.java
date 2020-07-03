package com.karthika.numberflip;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private static int ROW_COUNT = -1;
    private static int COL_COUNT = -1;
    private Context context;
    private int[][] cards;
    private List<Integer> numbers;
    private Card firstCard;
    private Card seconedCard;
    private ButtonListener buttonListener;
    private Button button;
    private static Float scale;
    private static Object lock = new Object();

    int turns;
    private TableLayout mainTable;
    private UpdateCardsHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new UpdateCardsHandler();
        loadImages();


        setContentView(R.layout.activity_main);

        buttonListener = new ButtonListener();

        mainTable = (TableLayout) findViewById(R.id.TableLayout03);

        context = mainTable.getContext();

        newGame(4, 5);
    }


    private void newGame(int c, int r) {
        ROW_COUNT = r;
        COL_COUNT = c;

        cards = new int[COL_COUNT][ROW_COUNT];

        for (int y = 0; y < ROW_COUNT; y++) {
            mainTable.addView(createRow(y));
        }

        firstCard = null;
        loadCards();

        turns = 0;
        /*((TextView) findViewById(R.id.tv1)).setText("Tries: " + turns);*/


    }

    private void loadImages() {
        numbers = new ArrayList<Integer>();
        numbers.add(10);
        numbers.add(18);
        numbers.add(24);
        numbers.add(32);
        numbers.add(28);
        numbers.add(95);
        numbers.add(88);
        numbers.add(76);
        numbers.add(53);
        numbers.add(44);
        numbers.add(18);
        numbers.add(44);
        numbers.add(32);
        numbers.add(95);
        numbers.add(88);
        numbers.add(28);
        numbers.add(10);
        numbers.add(76);
        numbers.add(53);
        numbers.add(24);
    }

    private void loadCards() {
        try {
            int size = ROW_COUNT * COL_COUNT;

            Log.i("loadCards()", "size=" + size);

            ArrayList<Integer> list = new ArrayList<Integer>();

            for (int i = 0; i < size; i++) {
                list.add(new Integer(i));
            }


            Random r = new Random();

            for (int i = size - 1; i >= 0; i--) {
                int t = 0;

                if (i > 0) {
                    t = r.nextInt(i);
                }

                t = list.remove(t).intValue();
                cards[i % COL_COUNT][i / COL_COUNT] = t % (size / 2);

                Log.i("loadCards()", "card[" + (i % COL_COUNT) +
                        "][" + (i / COL_COUNT) + "]=" + cards[i % COL_COUNT][i / COL_COUNT]);
            }
        } catch (Exception e) {
            Log.e("loadCards()", e + "");
        }

    }

    private TableRow createRow(int y) {
        TableRow row = new TableRow(context);
        row.setHorizontalGravity(Gravity.CENTER);

        for (int x = 0; x < COL_COUNT; x++) {
            row.addView(createButton(x, y));
        }
        return row;
    }


    private View createButton(int x, int y) {
        button = new Button(context);
        button.setTextSize(28);
        button.setTextColor(getColor(R.color.white));
        button.setBackgroundResource(R.drawable.unselected_rectangle);
        button.setId(100 * x + y);
        button.setOnClickListener(buttonListener);
        return button;
    }

    class ButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            synchronized (lock) {
                if (firstCard != null && seconedCard != null) {
                    return;
                }
                int id = v.getId();
                int x = id / 100;
                int y = id % 100;
                turnCard((Button) v, x, y);
            }

        }

        private void turnCard(Button button, int x, int y) {
//          button.setBackgroundDrawable(numbers.get(cards[x][y]));
            button.setBackgroundResource(R.drawable.selected_rectangle);
            String mmm = numbers.get(cards[x][y]).toString();
            button.setText(mmm);
            if (firstCard == null) {
                firstCard = new Card(button, x, y);
            } else {

                if (firstCard.x == x && firstCard.y == y) {
                    return;
                }

                seconedCard = new Card(button, x, y);

                turns++;
                /*((TextView) findViewById(R.id.tv1)).setText("Tries: " + turns);*/

                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            synchronized (lock) {
                                handler.sendEmptyMessage(0);
                            }
                        } catch (Exception e) {
                            Log.e("E1", e.getMessage());
                        }
                    }
                };

                Timer t = new Timer(false);
                t.schedule(tt, 1300);
            }


        }

    }

    class UpdateCardsHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            synchronized (lock) {
                checkCards();
            }
        }

        public void checkCards() {
            if (cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]) {
                firstCard.button.setBackgroundResource(R.drawable.selected_rectangle);
                seconedCard.button.setBackgroundResource(R.drawable.selected_rectangle);
            } else {
                seconedCard.button.setBackgroundResource(R.drawable.unselected_rectangle);
                seconedCard.button.setText("");
                firstCard.button.setBackgroundResource(R.drawable.unselected_rectangle);
                firstCard.button.setText("");
            }

            firstCard = null;
            seconedCard = null;
        }
    }


    class Card {

        public int x;
        public int y;
        public Button button;

        public Card(Button button, int x, int y) {
            this.x = x;
            this.y = y;
            this.button = button;
        }
    }
}
