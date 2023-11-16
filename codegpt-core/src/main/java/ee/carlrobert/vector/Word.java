package ee.carlrobert.vector;

import com.github.jelmerk.knn.Item;
import java.util.Arrays;

public class Word implements Item<Object, double[]> {

  private static final long serialVersionUID = 1L;

  private final String id;
  private final String meta;
  private final double[] vector;

  public Word(String id, String meta, double[] vector) {
    this.id = id;
    this.meta = meta;
    this.vector = vector;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public double[] vector() {
    return vector;
  }

  @Override
  public int dimensions() {
    return vector.length;
  }

  @Override
  public String toString() {
    return "Word{"
        + "id='" + id + '\''
        + ", vector=" + Arrays.toString(vector)
        + '}';
  }

  public String getMeta() {
    return meta;
  }
}