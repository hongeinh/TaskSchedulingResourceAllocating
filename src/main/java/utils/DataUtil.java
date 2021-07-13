package utils;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataUtil {
	private static final Logger logger = LoggerFactory.getLogger(DataUtil.class);

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
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}

			}
			if (oos != null) {
				try {
					oos.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
			if (bais != null) {
				try {
					bais.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}

		return deepCopy;
	}
}
