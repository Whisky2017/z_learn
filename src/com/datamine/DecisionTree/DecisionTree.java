package com.datamine.DecisionTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecisionTree {

    public TreeNode createDT(List<ArrayList<String>> data,List<String> attributeList){
        
        System.out.println("当前的DATA为");
        for(int i=0;i<data.size();i++){
            ArrayList<String> temp = data.get(i);
            for(int j=0;j<temp.size();j++){
                System.out.print(temp.get(j)+ " ");
            }
            System.out.println();
        }
        System.out.println("---------------------------------");
        System.out.println("当前的ATTR为");
        for(int i=0;i<attributeList.size();i++){
            System.out.print(attributeList.get(i)+ " ");
        }
        System.out.println();
        System.out.println("---------------------------------");
        
        TreeNode node = new TreeNode();
        String result = InfoGain.IsPure(InfoGain.getTarget(data)); //获取数据的target 并判断是否为一种结果
        if(result != null){
            node.setNodeName("leafNode");
            node.setTargetFunValue(result);
            return node;
        }
        if(attributeList.size() == 0){
            node.setTargetFunValue(result);
            return node;
        }else{
            InfoGain gain = new InfoGain(data,attributeList); //计算信息增益率信息
            double maxGain = 0.0;
            int attrIndex = -1; //最大信息增益率的下标
            for(int i=0;i<attributeList.size();i++){
                double tempGain = gain.getGainRatio(i); //计算信息增益率
                if(maxGain < tempGain){
                    maxGain = tempGain;
                    attrIndex = i;
                }
            }
            System.out.println("选择出的最大增益率属性为： " + attributeList.get(attrIndex));
            node.setAttributeValue(attributeList.get(attrIndex));
            List<ArrayList<String>> resultData = null;
            Map<String,Long> attrvalueMap = gain.getAttributeValue(attrIndex); //返回map<属性,属性数量>
            for(Map.Entry<String, Long> entry : attrvalueMap.entrySet()){
                resultData = gain.getData4Value(entry.getKey(), attrIndex); //根据 属性和下标 获取相应的数据
                TreeNode leafNode = null;
                System.out.println("当前为"+attributeList.get(attrIndex)+"的"+entry.getKey()+"分支。");
                if(resultData.size() == 0){
                    leafNode = new TreeNode();
                    leafNode.setNodeName(attributeList.get(attrIndex));
                    leafNode.setTargetFunValue(result);
                    leafNode.setAttributeValue(entry.getKey());
                }else{
                    for (int j = 0; j < resultData.size(); j++) {
                        resultData.get(j).remove(attrIndex);
                    }
                    ArrayList<String> resultAttr = new ArrayList<String>(attributeList);
                    resultAttr.remove(attrIndex);
                    leafNode = createDT(resultData,resultAttr);
                }
                node.getChildTreeNode().add(leafNode);
                node.getPathName().add(entry.getKey());
            }
        }
        return node;
    }
    
    class TreeNode{
        
        private String attributeValue; //属性名
        private List<TreeNode> childTreeNode; //孩子节点
        private List<String> pathName; //路径名
        private String targetFunValue;  //target的值
        private String nodeName; //是否是叶子
        
        public TreeNode(String nodeName){
            
            this.nodeName = nodeName;
            this.childTreeNode = new ArrayList<TreeNode>();
            this.pathName = new ArrayList<String>();
        }
        
        public TreeNode(){
            this.childTreeNode = new ArrayList<TreeNode>();
            this.pathName = new ArrayList<String>();
        }

        public String getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        public List<TreeNode> getChildTreeNode() {
            return childTreeNode;
        }

        public void setChildTreeNode(List<TreeNode> childTreeNode) {
            this.childTreeNode = childTreeNode;
        }

        public String getTargetFunValue() {
            return targetFunValue;
        }

        public void setTargetFunValue(String targetFunValue) {
            this.targetFunValue = targetFunValue;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public List<String> getPathName() {
            return pathName;
        }

        public void setPathName(List<String> pathName) {
            this.pathName = pathName;
        }
        
    }
}
