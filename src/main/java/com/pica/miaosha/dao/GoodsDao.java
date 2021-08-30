package com.pica.miaosha.dao;

import com.pica.miaosha.domian.MiaoshaGoods;
import com.pica.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {
    //查出秒杀商品
    @Select("select g.*, mg.miaosha_price, mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id")
    public List<GoodsVo> listGoodsVo();
    @Select("select g.*, mg.miaosha_price, mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id = g.id where g.id =#{goodsId} ")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id=#{goodsId} and stock_count > 0")
    public int reduceStock(MiaoshaGoods g);
}

