package szekelyistvan.com.colorpalette.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Palette implements Parcelable {
    private int id;
    private String title;
    private String userName;
    private int numViews;
    private int numVotes;
    private int numComments;
    private double numHearts;
    private int rank;
    private String dateCreated;
    private List<String> colors;
    private List<Double> colorWidths;
    private String description;
    private String url;
    private String imageUrl;
    private String badgeUrl;
    private String apiUrl;

    public Palette(String title, List<String> colors, String url) {
        this.title = title;
        this.colors = colors;
        this.url = url;
    }

        public String getTitle() {
            return title;
        }

        public List<String> getColors() {
            return colors;
        }

        public String getUrl() {
            return url;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.title);
            dest.writeString(this.userName);
            dest.writeInt(this.numViews);
            dest.writeInt(this.numVotes);
            dest.writeInt(this.numComments);
            dest.writeDouble(this.numHearts);
            dest.writeInt(this.rank);
            dest.writeString(this.dateCreated);
            dest.writeStringList(this.colors);
            dest.writeList(this.colorWidths);
            dest.writeString(this.description);
            dest.writeString(this.url);
            dest.writeString(this.imageUrl);
            dest.writeString(this.badgeUrl);
            dest.writeString(this.apiUrl);
        }

        public Palette() {
        }

        protected Palette(Parcel in) {
            this.id = in.readInt();
            this.title = in.readString();
            this.userName = in.readString();
            this.numViews = in.readInt();
            this.numVotes = in.readInt();
            this.numComments = in.readInt();
            this.numHearts = in.readDouble();
            this.rank = in.readInt();
            this.dateCreated = in.readString();
            this.colors = in.createStringArrayList();
            //noinspection Convert2Diamond
            this.colorWidths = new ArrayList<Double>();
            in.readList(this.colorWidths, Double.class.getClassLoader());
            this.description = in.readString();
            this.url = in.readString();
            this.imageUrl = in.readString();
            this.badgeUrl = in.readString();
            this.apiUrl = in.readString();
        }

        public static final Parcelable.Creator<Palette> CREATOR = new Parcelable.Creator<Palette>() {
            @Override
            public Palette createFromParcel(Parcel source) {
                return new Palette(source);
            }

            @Override
            public Palette[] newArray(int size) {
                return new Palette[size];
            }
        };
}
