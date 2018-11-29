package preprocesing;


import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param path The path to the folder containing the combined Netflix files
     * @param filename The filename without the number and format e.g. combined_data_
     * @param parts Number of seperate files e.g. 4
     * @param fileOut The name of the output file
     * @throws IOException
     */
  public static void transformNetflix(String path, String filename, int parts, String fileOut) throws IOException {

      BufferedWriter writer = new BufferedWriter(new FileWriter(fileOut));
      String movie="";
      String userID="";
      String rating="";

      for(int i=1; i<=parts; i++) { // For all 4 combined files
          File file = new File(path + filename + i + ".txt");
          try (BufferedReader br = new BufferedReader(new FileReader(file))) {

              String st;
              while ((st = br.readLine()) != null) {
                  if (st.endsWith(":")) {
                      movie = st.replace(":", "");
                  } else {
                      userID = st.split(",")[0];
                      rating = st.split(",")[1];
                  }
                  writer.write(userID + "," + movie + "," + rating + "\n");
              }
          }
      }
      writer.close();
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

        writeToFile(ratings, writer);

        writer.close();
    }
  }

    /**
     * Transforms Netflix combined files to COO format
     * @param fileName The name of the input file without number e.g. combined_data_
     * @throws IOException
     */
    public static void listsNetflix(String fileName) throws IOException {
        Map<String,List<MovieRating>> ratings = new HashMap<>();
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileName+".list"));

        for(int i=0; i<=4; i++) { // For each combined file
            File file = new File(fileName);
            try (BufferedReader br = new BufferedReader(new FileReader(file + String.valueOf(i) + ".txt"))) {
                String movie="";
                String userID="";
                String rating="";
                String st;
                while ((st = br.readLine()) != null) {
                    //System.out.println(st);
                    if (st.endsWith(":")) {
                        movie = st.replace(":", "");
                    } else {
                        userID = st.split(",")[0];
                        rating = st.split(",")[1];
                    }
                    MovieRating movieRating = new MovieRating(movie, rating);
                    List<MovieRating> userRatings = ratings.get(userID);
                    if (userRatings == null) {
                        userRatings = new LinkedList<>();
                        ratings.put(userID, userRatings);
                    }
                    userRatings.add(movieRating);
                }
            }
        }

        writeToFile(ratings, writer);

        writer.close();
    }

    private static void writeToFile(Map<String, List<MovieRating>> ratings, BufferedWriter writer) throws IOException {
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
