package com.cc.express.service;

import com.cc.express.dao.GraphDao;
import com.cc.express.entity.GraphEntity;
import com.cc.express.entity.unjsonfy.EdgeCost;
import com.cc.express.entity.unjsonfy.Goods;
import com.cc.express.entity.unjsonfy.Graph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GraphService {

    @Autowired
    GraphDao graphDao;

    private GraphEntity convert(Graph g) {
        GraphEntity graphEntity = new GraphEntity();

        graphEntity.setId(g.getId());
        graphEntity.setName(g.getName());
        graphEntity.setIsdelete(g.getIsDelete());

        if (g.getEdgeCostList() != null && !g.getEdgeCostList().isEmpty()) {
            var toList = new ArrayList<Integer>();
            var timeList = new ArrayList<Integer>();
            var feeList = new ArrayList<Integer>();
            var capacityList = new ArrayList<Integer>();

            for (var i : g.getEdgeCostList()) {
                toList.add(i.getToId());
                timeList.add(i.getTime());
                feeList.add(i.getFee());
                capacityList.add(i.getCapacity());
            }

            String to = toList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setTo(to);

            String time = timeList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setTimecost(time);

            String fee = feeList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setExpressfee(fee);

            String capacity = capacityList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setCapacity(capacity);

        }

        if (g.getGoodsList() != null && !g.getGoodsList().isEmpty()) {
            var nameList = new ArrayList<String>();
            var amountList = new ArrayList<Integer>();
            var thresholdList = new ArrayList<Integer>();

            for (var i : g.getGoodsList()) {
                nameList.add(i.getName());
                amountList.add(i.getAmount());
                thresholdList.add(i.getThreshold());
            }

            String name = String.join(",", nameList);
            graphEntity.setGoods(name);

            String amount = amountList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setGoodsamount(amount);

            String threshold = thresholdList.stream().map(Object::toString).collect(Collectors.joining(","));
            graphEntity.setGoodsthreshold(threshold);
        }

        return graphEntity;
    }


    private Graph convert(GraphEntity graph) {
        Graph g = new Graph();
        g.setId(graph.getId());
        g.setName(graph.getName());
        g.setIsDelete(graph.getIsdelete());

        List<Integer> toList = null;
        List<Integer> timeList = null;
        List<Integer> feeList = null;
        List<Integer> capacityList = null;

        if (!graph.getTo().isEmpty()) {
            String[] strArray = graph.getTo().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            toList = Arrays.stream(intArray).boxed().toList();
        }

        if (!graph.getTimecost().isEmpty()) {
            String[] strArray = graph.getTimecost().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            timeList = Arrays.stream(intArray).boxed().toList();
        }

        if (!graph.getExpressfee().isEmpty()) {
            String[] strArray = graph.getExpressfee().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            feeList = Arrays.stream(intArray).boxed().toList();
        }

        if (!graph.getCapacity().isEmpty()) {
            String[] strArray = graph.getCapacity().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            capacityList = Arrays.stream(intArray).boxed().toList();
        }

        List<EdgeCost> edgeCostList = new ArrayList<>();

        if (toList != null && timeList != null && feeList != null && capacityList != null) {
            if (toList.size() == timeList.size() && timeList.size() == feeList.size() && feeList.size() == capacityList.size()) {
                int size = toList.size();

                for (int i = 0; i < size; i++) {
                    var edgeCost = new EdgeCost();
                    edgeCost.setToId(toList.get(i));
                    edgeCost.setTime(timeList.get(i));
                    edgeCost.setFee(feeList.get(i));
                    edgeCost.setCapacity(capacityList.get(i));
                    edgeCostList.add(edgeCost);
                }
            }
        }

        g.setEdgeCostList(edgeCostList);

        List<String> goodsNameList = null;
        List<Integer> goodsAmountList = null;
        List<Integer> goodsThresholdList = null;

        if (!graph.getGoods().isEmpty()) {
            String[] strArray = graph.getGoods().split(",");
            goodsNameList = Arrays.asList(strArray);
        }

        if (!graph.getGoodsamount().isEmpty()) {
            String[] strArray = graph.getGoodsamount().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            goodsAmountList = Arrays.stream(intArray).boxed().toList();
        }

        if (!graph.getGoodsthreshold().isEmpty()) {
            String[] strArray = graph.getGoodsthreshold().split(",");
            int[] intArray = Arrays.stream(strArray).mapToInt(Integer::parseInt).toArray();
            goodsThresholdList = Arrays.stream(intArray).boxed().toList();
        }

        List<Goods> goodsList = new ArrayList<>();

        if (goodsNameList != null && goodsAmountList != null && goodsThresholdList != null) {
            if (goodsNameList.size() == goodsAmountList.size() && goodsAmountList.size() == goodsThresholdList.size()) {
                int size = goodsNameList.size();

                for (int i = 0; i < size; i++) {
                    var goods = new Goods();
                    goods.setName(goodsNameList.get(i));
                    goods.setAmount(goodsAmountList.get(i));
                    goods.setThreshold(goodsThresholdList.get(i));
                    goodsList.add(goods);
                }
            }
        }

        g.setGoodsList(goodsList);

        return g;
    }

    private List<Graph> convert(List<GraphEntity> graph) {
        List<Graph> g = new ArrayList<>();
        for (var i : graph) {
            g.add(convert(i));
        }
        return g;
    }

    public List<Graph> getAll() {
        return convert(graphDao.getAll());
    }

    public boolean modifyGoodsConfig(Integer id, String goods, Integer amount, Integer threshold) {
        Graph temp = convert(graphDao.getGraph(id));

        boolean haveGoods = false;

        var goodsList = temp.getGoodsList();
        for (int i = 0; i < goodsList.size(); i++) {
            if (Objects.equals(goodsList.get(i).getName(), goods)) {
                goodsList.get(i).setAmount(Math.max(0, goodsList.get(i).getAmount() + amount));
                goodsList.get(i).setThreshold(threshold);
                haveGoods = true;

                if (goodsList.get(i).getAmount() == 0) {
                    goodsList.remove(i);
                }
                break;
            }
        }

        if (!haveGoods) {
            var g = new Goods();
            g.setName(goods);
            g.setAmount(amount);
            g.setThreshold(threshold);
            temp.getGoodsList().add(g);
        }

        return graphDao.updateGraph(convert(temp));
    }

    public boolean modifyGoodsConfig(Integer id, String goods, Integer amount) {
        Graph temp = convert(graphDao.getGraph(id));

        boolean haveGoods = false;

        var goodsList = temp.getGoodsList();
        for (int i = 0; i < goodsList.size(); i++) {
            if (Objects.equals(goodsList.get(i).getName(), goods)) {
                goodsList.get(i).setAmount(Math.max(0, goodsList.get(i).getAmount() + amount));
                haveGoods = true;

                if (goodsList.get(i).getAmount() == 0) {
                    goodsList.remove(i);
                }
                break;
            }
        }

        if (!haveGoods) {
            var g = new Goods();
            g.setName(goods);
            g.setAmount(amount);
            g.setThreshold(0);
            temp.getGoodsList().add(g);
        }

        return graphDao.updateGraph(convert(temp));
    }
}
