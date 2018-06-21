package szekelyistvan.com.colorpalette.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import szekelyistvan.com.colorpalette.R;
import szekelyistvan.com.colorpalette.model.Palette;

import static szekelyistvan.com.colorpalette.ui.MainActivity.PALETTE_OBJECT;

public class DetailActivity extends AppCompatActivity {

    private Palette receivedPalette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            receivedPalette = extras.getParcelable(PALETTE_OBJECT);
            setTitle(receivedPalette.getTitle());
        } else {
            finish();
            Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
        }
    }
}
