package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class DataUtil {

	public static <T> T cloneBean(T source) {
		if (source == null) {
			return null;
		}

		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		T deepCopy = null;
		try {


			baos = new ByteArrayOutputStream();


			oos = new ObjectOutputStream(baos);

			oos.writeObject(source);
			bais = new ByteArrayInputStream(baos.toByteArray());
			ois = new ObjectInputStream(bais);
			deepCopy = (T) ois.readObject();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
			if (oos != null) {
				try {
					oos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		return deepCopy;
	}

	public static boolean nullOrEmpty(Collection objects) {
		return objects == null || objects.isEmpty();
	}

	public static boolean nonNullOrEmpty(Collection objects) {
		return !nullOrEmpty(objects);
	}


}
