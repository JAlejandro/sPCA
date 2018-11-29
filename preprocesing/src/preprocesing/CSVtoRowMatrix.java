package preprocesing;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.list.TreeList;

public class CSVtoRowMatrix {
  public static class MovieRating {
    String movie;
    String rating;

    public MovieRating(String movie, String rating){
      this.movie = movie;
      this.rating = rating;
    }

    @Override
    public String toString(){
      return movie+","+rating;
    }
  }

  public static void transform(String fileName, String fileOut) throws IOException {

    File file = new File(fileName);
    BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

      String st;
      while ((st = br.readLine()) != null) {
        //System.out.println(st);
        String[] splits = st.split(",");
        String userID = splits[0];
        String movie = splits[1];
        String rating = splits[2];

        writer.write(userID + "," + movie + "," + rating + "\n");
      }
      writer.close();
    }
  }

  public static void lists(String fileName) throws IOException {
    Map<String,List<MovieRating>> ratings = new HashMap<>();
    File file = new File(fileName);

    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName+".list"));
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {

      String st;
      while ((st = br.readLine()) != null) {
        //System.out.println(st);
        String[] splits = st.split(",");
        String userID = splits[0];
        String movie = splits[1];
        String rating = splits[2];
        MovieRating movieRating = new MovieRating(movie,rating);
        List<MovieRating> userRatings = ratings.get(userID);
        if(userRatings == null) {
          userRatings = new LinkedList<>();
          ratings.put(userID,userRatings);
        }
        userRatings.add(movieRating);
      }

      for(String id : ratings.keySet()) {
        LinkedList<String> movieId = new LinkedList<>();
        LinkedList<String> rating = new LinkedList<>();

        writer.write(id + "#");
        for(MovieRating mr : ratings.get(id)) {
          movieId.add(mr.movie);
          rating.add(mr.rating);
        }
        writer.write(movieId.toString());
        writer.write("#");
        writer.write(rating.toString());
        writer.write("\n");
      }

      writer.close();
    }
  }

  public static void main(String[] args) {
    String fileName = "/home/jacamino/Downloads/ml-20m/ratingss.csv";
    try {
      //transform(fileName, fileName+".out");
      lists(fileName);
    } catch (IOException e){
      e.printStackTrace();
    }

  }
}
