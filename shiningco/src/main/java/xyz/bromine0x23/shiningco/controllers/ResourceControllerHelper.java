package xyz.bromine0x23.shiningco.controllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ResourceControllerHelper {

	/**
	 * 将实体对象转换为表示对象
	 *
	 * @param entity            实体对象
	 * @param mapper            转换方法
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @return 表示对象
	 */
	default <TEntity, TRepresentation> TRepresentation toRepresentation(
		TEntity entity,
		Function<? super TEntity, ? extends TRepresentation> mapper
	) {
		return mapper.apply(entity);
	}

	/**
	 * 将实体对象数组转换为表示对象列表
	 *
	 * @param entities          实体对象数组
	 * @param mapper            转换方法
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation> List<TRepresentation> toRepresentations(
		TEntity[] entities,
		Function<? super TEntity, ? extends TRepresentation> mapper
	) {
		return toRepresentations(Arrays.stream(entities), mapper);
	}

	/**
	 * 将实体对象数组转换为表示对象列表
	 *
	 * @param entities          实体对象数组
	 * @param mapper            转换方法
	 * @param collector         收集器
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @param <TCollection>     集合类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation, TCollection extends Collection<TRepresentation>> TCollection toRepresentations(
		TEntity[] entities,
		Function<? super TEntity, ? extends TRepresentation> mapper,
		Collector<? super TRepresentation, ?, TCollection> collector
	) {
		return toRepresentations(Arrays.stream(entities), mapper, collector);
	}

	/**
	 * 将实体对象列表转换为表示对象列表
	 *
	 * @param entities          实体对象列表
	 * @param mapper            转换方法
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation> List<TRepresentation> toRepresentations(
		Collection<TEntity> entities,
		Function<? super TEntity, ? extends TRepresentation> mapper
	) {
		return toRepresentations(entities.stream(), mapper);
	}

	/**
	 * 将实体对象列表转换为表示对象列表
	 *
	 * @param entities          实体对象列表
	 * @param mapper            转换方法
	 * @param collector         收集器
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @param <TCollection>     集合类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation, TCollection extends Collection<TRepresentation>> TCollection toRepresentations(
		Collection<TEntity> entities,
		Function<? super TEntity, ? extends TRepresentation> mapper,
		Collector<? super TRepresentation, ?, TCollection> collector
	) {
		return toRepresentations(entities.stream(), mapper, collector);
	}

	/**
	 * 将实体对象流转换为表示对象列表
	 *
	 * @param entities          实体对象流
	 * @param mapper            转换方法
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation> List<TRepresentation> toRepresentations(
		Stream<TEntity> entities,
		Function<? super TEntity, ? extends TRepresentation> mapper
	) {
		return toRepresentations(entities, mapper, Collectors.toList());
	}

	/**
	 * 将实体对象流转换为表示对象列表
	 *
	 * @param entities          实体对象流
	 * @param mapper            转换方法
	 * @param collector         收集器
	 * @param <TEntity>         实体类类型
	 * @param <TRepresentation> 表示类类型
	 * @param <TCollection>     集合类类型
	 * @return 表示对象列表
	 */
	default <TEntity, TRepresentation, TCollection extends Collection<TRepresentation>> TCollection toRepresentations(
		Stream<TEntity> entities,
		Function<? super TEntity, ? extends TRepresentation> mapper,
		Collector<? super TRepresentation, ?, TCollection> collector
	) {
		return entities.map(mapper).collect(collector);
	}

}
