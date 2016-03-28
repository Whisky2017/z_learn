package com.datamine.CollaborativeFiltering;

import java.io.File;
import java.util.List;

import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * 基于物品的协同过滤
 * @author Administrator
 *
 */
public class ItemCF {

	final static int RECOMMENDER_NUM = 3; //推荐物品数目
	
	public static void main(String[] args) throws Exception {
		
		File file = new File("data/CF.data");
		
		//构造数据模型
		DataModel model = new FileDataModel(file); 
		
		//使用皮尔松算法 计算内容之间的相似度
		ItemSimilarity itemSimilarity = new PearsonCorrelationSimilarity(model); 
		
		//构造推荐引擎 基于物品的推荐
		Recommender r = new GenericItemBasedRecommender(model, itemSimilarity);
		
		//迭代获取用户的id
		LongPrimitiveIterator it = model.getUserIDs();
		
		while(it.hasNext()){
			long uid = it.nextLong();
			//获得推荐结果，给userID推荐howMany个Item
			List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
			System.out.printf("uid:%s",uid);
			for(RecommendedItem items : list){
				System.out.printf("(%s,%f)",items.getItemID(),items.getValue());
			}
			System.out.println();
		}
		
	}
	
}
