package com.datamine.CollaborativeFiltering;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

/**
 * 评分模型
 * @author Administrator
 *
 */
public class RecommenderEvaluatorStudy {

	public static void main(String[] args) throws Exception {

		// 每次生成的随机数都相同
		// 因此随机生成可以重复的结果
		// 这里是为了测试，实际代码中请勿使用
		//RandomUtils.useTestSeed();

		// 构建推荐的数据模型
		DataModel model = new FileDataModel(new File("data\\CF.data"));

		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		
		RecommenderBuilder builder = new RecommenderBuilder() {
			@Override
			public Recommender buildRecommender(DataModel model)
					throws TasteException {
				UserSimilarity similarity = new PearsonCorrelationSimilarity(
						model);
				UserNeighborhood neighborhood = new NearestNUserNeighborhood(3,
						similarity, model);
				return new GenericUserBasedRecommender(model, neighborhood,
						similarity);
			}
		};

		// 这里的数据意思是训练70%，测试30%的数据
		// 这里的数据如果显示出现了NAN，就表示计算数据出现了问题NAN: not a number
		// 你只需要修改一下参数，我这里改成了0.9
		double score = evaluator.evaluate(builder, null, model, 0.8, 1.0);
		System.out.println("分值为：" + score);
		
	}
	
}
