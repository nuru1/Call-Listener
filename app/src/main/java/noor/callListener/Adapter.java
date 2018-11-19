package noor.callListener;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Currency;


public class Adapter extends ArrayAdapter<Numbers> {

    Context context;
    ArrayList<Numbers> numbers;
    DatabaseHelper db;

    public Adapter(@NonNull Context context, ArrayList<Numbers> numbers) {
        super(context, R.layout.each_number,numbers);
        this.context = context;
        this.numbers = numbers;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem==null)
            listItem = LayoutInflater.from(context).inflate(R.layout.each_number,parent,false);

        Numbers num = numbers.get(position);

        TextView name = (TextView)listItem.findViewById(R.id.name);
        TextView number = (TextView)listItem.findViewById(R.id.number);

        name.setText(num.getName());
        number.setText(num.getNumber());

        return listItem;
    }

    private ArrayList<Numbers> UpdateData() {
        numbers.clear();
        db = new DatabaseHelper(context);
        Cursor cursor = db.GetNumbers();
        while (cursor.moveToNext()){
            numbers.add(new Numbers(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
            Log.e("Cursor count",cursor.getString(0)+" "+cursor.getString(2)+" "+cursor.getString(1));
        }
        return numbers;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        UpdateData();
    }
}
