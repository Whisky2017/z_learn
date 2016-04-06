package com.datamine.CollaborativeFiltering;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * 基于用户的协同过滤  
 * 利用mahout 8.0
 * @author Administrator
 *
 */
public class UserCF {

	final static int NEIGHBORHOOD_NUM = 3; //邻居数据
	final static int RECOMMENDER_NUM = 7; //推荐物品数目
	
	public static void main(String[] args) throws Exception {
		
		
		/*
		 *	FileDataModel支持文件的读取，Mahout对文件的格式没有太多严格的要求，只要满足一下格式就OK：
		 * 1、每一行包含一个用户Id，物品Id，用户喜好
		 * 2、逗号隔开或者Tab隔开
		 * 3、*.zip 和 *.gz 文件会自动解压缩（Mahout 建议在数据量过大时采用压缩的数据存储）
		 *	FileDataModel从文件中读取数据，然后将数据以GenericDataModel的形式载入内存，具体可以查看FileDataModel中的buildModel方法。 
		 */
		String file = "data/CF.data";
		
		//建立数据模型
		DataModel datamodel = new FileDataModel(new File(file));
		
		//忽略偏好值
		GenericBooleanPrefDataModel model = new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(datamodel));
		
		//使用皮尔松算法计算用户之间的相似度
		//UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
		
		//忽略偏好值
		UserSimilarity userSimilarity = new TanimotoCoefficientSimilarity(model);
		
		//通过相似度 和N 计算出最近邻N个用户
		NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, userSimilarity, model);
	
		//够着推荐引擎  基于用户的推荐算法 推荐
		Recommender r = new GenericUserBasedRecommender(model, neighbor, userSimilarity);
		
		//迭代获取用户的id
		LongPrimitiveIterator it = model.getUserIDs();
		
		while(it.hasNext()){
			
			long uid = it.nextLong();
			//获得推荐结果，给userID推荐howMany个Item
			List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
			System.out.printf("uid:%s",uid);
			for(RecommendedItem ritem : list){
				System.out.printf("(%s,%f)",ritem.getItemID(),ritem.getValue());
			}
			System.out.println();
		}
	}

	
}
