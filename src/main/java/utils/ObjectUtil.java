package utils;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Array;
import java.util.*;

import static utils.DataUtil.nullOrEmpty;

public class ObjectUtil {

	public static boolean isNull(Object obj) {
		return obj == null;
	}

	public static boolean nonNull(Object obj) {
		return obj != null;
	}

	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		} else if (obj instanceof Optional) {
			return !((Optional) obj).isPresent();
		} else if (obj instanceof CharSequence) {
			return ((CharSequence) obj).length() == 0;
		} else if (obj.getClass().isArray()) {
			return Array.getLength(obj) == 0;
		} else if (obj instanceof Collection) {
			return ((Collection) obj).isEmpty();
		} else {
			return obj instanceof Map && ((Map) obj).isEmpty();
		}
	}


	public static <S, D> void copyProperties(S source, D destination) {
		if (isNull(source) || isNull(destination)) {
			return;
		}
		final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap((Class<S>) source.getClass(), (Class<D>) destination.getClass())
				.constructorA()
				.constructorB()
				.byDefault()
				.register();
		MapperFacade mapper = mapperFactory.getMapperFacade();
		mapper.map(source, destination);
	}

	public static <S, D> D copyProperties(S source, Class<D> destination) {
		if (isNull(source)) {
			return null;
		}
		final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
		mapperFactory.classMap((Class<S>) source.getClass(), destination  )
				.constructorA()
				.constructorB()
				.byDefault()
				.register();
		MapperFacade mapper = mapperFactory.getMapperFacade();
		return mapper.map(source, destination);
	}

	public static <S, D> List<D> copyProperties(List<S> sources, Class<D> classDestination) {
		if (nullOrEmpty(sources)) {
			return new ArrayList<>();
		}
		List<D> results = new ArrayList<>();
		sources.forEach(source -> results.add(copyProperties(source, classDestination)));
		return results;
	}

	/**
	 * Returns an array of null properties of an object
	 *
	 * @param source
	 * @return
	 * */
	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set emptyNames = new HashSet();
		for (java.beans.PropertyDescriptor pd: pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) {
				emptyNames.add(srcValue);
			}
		}
		String[] results = new String[emptyNames.size()];
		return (String[]) emptyNames.toArray(results);
	}
}
