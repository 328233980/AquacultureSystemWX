package com.aquaculture.service.impl;

import com.aquaculture.dto.request.FeedingCalculatorRequest;
import com.aquaculture.dto.response.FeedingCalculatorResponse;
import com.aquaculture.service.CalculatorService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculatorServiceImpl implements CalculatorService {

    @Override
    public FeedingCalculatorResponse calculateFeeding(FeedingCalculatorRequest request) {
        FeedingCalculatorResponse response = new FeedingCalculatorResponse();
        
        BigDecimal biomass = request.getBiomass();
        String species = request.getSpecies();
        String growthStage = request.getGrowthStage();
        BigDecimal waterTemp = request.getWaterTemp();

        // 根据品种和生长阶段确定基础投饵率
        BigDecimal baseFeedingRate = getBaseFeedingRate(species, growthStage);
        
        // 根据水温调整系数
        BigDecimal tempCoefficient = getTempCoefficient(species, waterTemp);
        
        // 计算最终投饵率
        BigDecimal feedingRate = baseFeedingRate.multiply(tempCoefficient);
        
        // 计算推荐投喂量 = 生物量 × 投饵率 / 100
        BigDecimal recommendedAmount = biomass.multiply(feedingRate)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        
        // 建议投喂次数
        int feedingTimes = getFeedingTimes(growthStage, waterTemp);
        
        // 注意事项
        String notes = generateNotes(species, growthStage, waterTemp);

        response.setRecommendedAmount(recommendedAmount);
        response.setFeedingRate(feedingRate.setScale(2, RoundingMode.HALF_UP));
        response.setFeedingTimes(feedingTimes);
        response.setNotes(notes);

        return response;
    }

    private BigDecimal getBaseFeedingRate(String species, String growthStage) {
        // 虾类投饵率
        if ("shrimp".equals(species)) {
            switch (growthStage) {
                case "early":
                    return new BigDecimal("6.5"); // 5-8%
                case "middle":
                    return new BigDecimal("4.0"); // 3-5%
                case "late":
                    return new BigDecimal("2.5"); // 2-3%
                default:
                    return new BigDecimal("4.0");
            }
        }
        // 蟹类投饵率
        else if ("crab".equals(species)) {
            switch (growthStage) {
                case "early":
                    return new BigDecimal("8.0"); // 6-10%
                case "middle":
                    return new BigDecimal("5.0"); // 4-6%
                case "late":
                    return new BigDecimal("3.0"); // 2-4%
                default:
                    return new BigDecimal("5.0");
            }
        }
        // 默认
        return new BigDecimal("4.0");
    }

    private BigDecimal getTempCoefficient(String species, BigDecimal waterTemp) {
        if (waterTemp == null) {
            return BigDecimal.ONE;
        }

        double temp = waterTemp.doubleValue();

        if ("shrimp".equals(species)) {
            // 虾类最适水温25-30°C
            if (temp >= 25 && temp <= 30) {
                return BigDecimal.ONE;
            } else if (temp >= 20 && temp < 25) {
                return new BigDecimal("0.8");
            } else if (temp > 30 && temp <= 33) {
                return new BigDecimal("0.9");
            } else if (temp < 20) {
                return new BigDecimal("0.5");
            } else {
                return new BigDecimal("0.6");
            }
        } else if ("crab".equals(species)) {
            // 蟹类最适水温22-28°C
            if (temp >= 22 && temp <= 28) {
                return BigDecimal.ONE;
            } else if (temp >= 18 && temp < 22) {
                return new BigDecimal("0.8");
            } else if (temp > 28 && temp <= 32) {
                return new BigDecimal("0.85");
            } else if (temp < 18) {
                return new BigDecimal("0.5");
            } else {
                return new BigDecimal("0.6");
            }
        }

        return BigDecimal.ONE;
    }

    private int getFeedingTimes(String growthStage, BigDecimal waterTemp) {
        int baseTimes;
        switch (growthStage) {
            case "early":
                baseTimes = 4; // 早期少量多次
                break;
            case "middle":
                baseTimes = 3;
                break;
            case "late":
                baseTimes = 2;
                break;
            default:
                baseTimes = 3;
        }

        // 高温时可适当增加投喂次数
        if (waterTemp != null && waterTemp.doubleValue() > 28) {
            baseTimes = Math.min(baseTimes + 1, 5);
        }

        return baseTimes;
    }

    private String generateNotes(String species, String growthStage, BigDecimal waterTemp) {
        StringBuilder notes = new StringBuilder();
        
        notes.append("建议投喂时间：早晨6-7点和傍晚5-6点为最佳投喂时间。");
        
        if (waterTemp != null) {
            double temp = waterTemp.doubleValue();
            if (temp < 20) {
                notes.append("当前水温较低，应减少投喂量，注意观察摄食情况。");
            } else if (temp > 32) {
                notes.append("当前水温较高，应在早晚凉爽时投喂，避免中午高温时段。");
            }
        }

        if ("early".equals(growthStage)) {
            notes.append("幼苗期建议使用优质开口料或破碎料，少量多次投喂。");
        } else if ("late".equals(growthStage)) {
            notes.append("养殖后期应控制投喂量，防止水质恶化。");
        }

        return notes.toString();
    }
}
