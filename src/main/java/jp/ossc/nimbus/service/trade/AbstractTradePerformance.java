/*
 * This software is distributed under following license based on modified BSD
 * style license.
 * ----------------------------------------------------------------------
 * 
 * Copyright 2003 The Nimbus Project. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE NIMBUS PROJECT ``AS IS'' AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE NIMBUS PROJECT OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the Nimbus Project.
 */
package jp.ossc.nimbus.service.trade;

import java.io.Serializable;
import java.util.*;

/**
 * 取引パフォーマンスの抽象クラス。<p>
 *
 * @author M.Takata
 */
public abstract class AbstractTradePerformance implements Serializable{
    
    private static final long serialVersionUID = 7769362582532741220L;
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうか。<p>
     * デフォルトは、trueで含める。<br>
     */
    protected boolean isContainsDrawToWin = true;
    
    /**
     * 総利益。<p>
     */
    protected double totalProfit;
    
    /**
     * 総利益率。<p>
     */
    protected double totalProfitRatio;
    
    /**
     * 保有中取引の総利益。<p>
     */
    protected double totalProfitInHolding;
    
    /**
     * 保有中取引の総利益率。<p>
     */
    protected double totalProfitRatioInHolding;
    
    /**
     * 総損失。<p>
     */
    protected double totalLoss;
    
    /**
     * 総損失率。<p>
     */
    protected double totalLossRatio;
    
    /**
     * 保有中取引の総損失。<p>
     */
    protected double totalLossInHolding;
    
    /**
     * 保有中取引の総損失率。<p>
     */
    protected double totalLossRatioInHolding;
    
    /**
     * 取引回数。<p>
     */
    protected int tradeNum;
    
    /**
     * 保有中取引の取引回数。<p>
     */
    protected int tradeNumInHolding;
    
    /**
     * 勝ち取引回数。<p>
     */
    protected int winTradeNum;
    
    /**
     * 保有中取引の勝ち取引回数。<p>
     */
    protected int winTradeNumInHolding;
    
    /**
     * 最大利益。<p>
     */
    protected double maxProfit;
    
    /**
     * 保有中取引の最大利益。<p>
     */
    protected double maxProfitInHolding;
    
    /**
     * 最大利益率。<p>
     */
    protected double maxProfitRatio;
    
    /**
     * 保有中取引の最大利益率。<p>
     */
    protected double maxProfitRatioInHolding;
    
    /**
     * 最大損失。<p>
     */
    protected double maxLoss;
    
    /**
     * 保有中取引の最大損失。<p>
     */
    protected double maxLossInHolding;
    
    /**
     * 最大損失率。<p>
     */
    protected double maxLossRatio;
    
    /**
     * 保有中取引の最大損失率。<p>
     */
    protected double maxLossRatioInHolding;
    
    /**
     * 取引対象開始日時。<p>
     */
    protected Date tradeTargetStartTime;
    
    /**
     * 取引対象終了日時。<p>
     */
    protected Date tradeTargetEndTime;
    
    /**
     * 総保有期間[ms]。<p>
     */
    protected long totalHoldingTermMillis;
    
    /**
     * 保有中取引の総保有期間[ms]。<p>
     */
    protected long totalHoldingTermMillisInHolding;
    
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうかを設定する。<p>
     * デフォルトは、trueで含める。<br>
     *
     * @param isContains 引き分けの取引を勝ち取引に含める場合は、true。引き分けの取引を負け取引に含める場合は、false
     */
    public void setContainsDrawToWin(boolean isContains){
        isContainsDrawToWin = isContains;
    }
    
    /**
     * 引き分けの取引を勝ち取引に含めるかどうかを判定する。<p>
     *
     * @return trueの場合は、引き分けの取引を勝ち取引に含める。falseの場合は、引き分けの取引を負け取引に含める
     */
    public boolean isContainsDrawToWin(){
        return isContainsDrawToWin;
    }
    
    /**
     * 総利益を取得する。<p>
     *
     * @return 総利益
     */
    public double getTotalProfit(){
        return totalProfit;
    }
    
    /**
     * 保有中取引の総利益を取得する。<p>
     *
     * @return 保有中取引の総利益
     */
    public double getTotalProfitInHolding(){
        return totalProfitInHolding;
    }
    
    /**
     * 保有中取引を含む総利益を取得する。<p>
     *
     * @return 保有中取引を含む総評価利益
     */
    public double getTotalProfitWithHolding(){
        return totalProfit + getTotalProfitInHolding();
    }
    
    /**
     * 平均利益を取得する。<p>
     *
     * @return 平均利益
     */
    public double getAverageProfit(){
        return winTradeNum == 0 ? 0d  : totalProfit / (double)winTradeNum;
    }
    
    /**
     * 保有中取引の平均利益を取得する。<p>
     *
     * @return 保有中取引の平均利益
     */
    public double getAverageProfitInHolding(){
        return getWinTradeNumInHolding() == 0 ? 0d  : (getTotalProfitInHolding() / (double)getWinTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均利益を取得する。<p>
     *
     * @return 保有中取引を含む平均利益
     */
    public double getAverageProfitWithHolding(){
        return getWinTradeNumWithHolding() == 0 ? 0d  : (getTotalProfitWithHolding() / (double)getWinTradeNumWithHolding());
    }
    
    /**
     * 最大利益を取得する。<p>
     *
     * @return 最大利益
     */
    public double getMaxProfit(){
        return maxProfit;
    }
    
    /**
     * 保有中取引の最大利益を取得する。<p>
     *
     * @return 保有中取引の最大利益
     */
    public double getMaxProfitInHolding(){
        return maxProfitInHolding;
    }
    
    /**
     * 保有中取引を含む最大利益を取得する。<p>
     *
     * @return 保有中取引を含む最大利益
     */
    public double getMaxProfitWithHolding(){
        return Math.max(maxProfit, getMaxProfitInHolding());
    }
    
    /**
     * 総利益率を取得する。<p>
     *
     * @return 総利益率
     */
    public double getTotalProfitRatio(){
        return totalProfitRatio;
    }
    
    /**
     * 保有中取引の総利益率を取得する。<p>
     *
     * @return 保有中取引の総利益率
     */
    public double getTotalProfitRatioInHolding(){
        return totalProfitRatioInHolding;
    }
    
    /**
     * 保有中取引を含む総利益率を取得する。<p>
     *
     * @return 保有中取引を含む総利益率
     */
    public double getTotalProfitRatioWithHolding(){
        return totalProfitRatio + getTotalProfitRatioInHolding();
    }
    
    /**
     * 平均利益率を取得する。<p>
     *
     * @return 平均利益率
     */
    public double getAverageProfitRatio(){
        return winTradeNum == 0 ? 0d  : totalProfitRatio / (double)winTradeNum;
    }
    
    /**
     * 保有中取引の平均利益率を取得する。<p>
     *
     * @return 保有中取引の平均利益率
     */
    public double getAverageProfitRatioInHolding(){
        return getWinTradeNumInHolding() == 0 ? 0d  : (getTotalProfitRatioInHolding() / (double)getWinTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均利益率を取得する。<p>
     *
     * @return 保有中取引を含む平均利益率
     */
    public double getAverageProfitRatioWithHolding(){
        return getWinTradeNumWithHolding() == 0 ? 0d  : (getTotalProfitRatioWithHolding() / (double)getWinTradeNumWithHolding());
    }
    
    /**
     * 最大利益率を取得する。<p>
     *
     * @return 最大利益率
     */
    public double getMaxProfitRatio(){
        return maxProfitRatio;
    }
    
    /**
     * 保有中取引の最大利益率を取得する。<p>
     *
     * @return 保有中取引の最大利益率
     */
    public double getMaxProfitRatioInHolding(){
        return maxProfitRatioInHolding;
    }
    
    /**
     * 保有中取引を含む最大利益率を取得する。<p>
     *
     * @return 保有中取引を含む最大利益率
     */
    public double getMaxProfitRatioWithHolding(){
        return Math.max(maxProfitRatio, getMaxProfitRatioInHolding());
    }
    
    /**
     * 総損失を取得する。<p>
     *
     * @return 総損失
     */
    public double getTotalLoss(){
        return totalLoss;
    }
    
    /**
     * 保有中取引の総損失を取得する。<p>
     *
     * @return 保有中取引の総損失
     */
    public double getTotalLossInHolding(){
        return totalLossInHolding;
    }
    
    /**
     * 保有中取引を含む総損失を取得する。<p>
     *
     * @return 保有中取引を含む総損失
     */
    public double getTotalLossWithHolding(){
        return totalLoss + getTotalLossInHolding();
    }
    
    /**
     * 平均損失を取得する。<p>
     *
     * @return 平均損失
     */
    public double getAverageLoss(){
        final int lossTradeNum = tradeNum - winTradeNum;
        return lossTradeNum == 0 ? 0d : totalLoss / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引の平均損失を取得する。<p>
     *
     * @return 保有中取引の平均損失
     */
    public double getAverageLossInHolding(){
        final int lossTradeNum = getTradeNumInHolding() - getWinTradeNumInHolding();
        return lossTradeNum == 0 ? 0d : getTotalLossInHolding() / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引を含む平均損失を取得する。<p>
     *
     * @return 保有中取引を含む平均損失
     */
    public double getAverageLossWithHolding(){
        final int lossTradeNum = getTradeNumWithHolding() - getWinTradeNumWithHolding();
        return lossTradeNum == 0 ? 0d : (getTotalLossWithHolding() / (double)lossTradeNum);
    }
    
    /**
     * 最大損失を取得する。<p>
     *
     * @return 最大損失
     */
    public double getMaxLoss(){
        return maxLoss;
    }
    
    /**
     * 保有中取引の最大損失を取得する。<p>
     *
     * @return 保有中取引の最大損失
     */
    public double getMaxLossInHolding(){
        return maxLossInHolding;
    }
    
    /**
     * 保有中取引を含む最大損失を取得する。<p>
     *
     * @return 保有中取引を含む最大損失
     */
    public double getMaxLossWithHolding(){
        return Math.max(maxLoss, getMaxLossInHolding());
    }
    
    /**
     * 総損失率を取得する。<p>
     *
     * @return 総損失率
     */
    public double getTotalLossRatio(){
        return totalLossRatio;
    }
    
    /**
     * 保有中取引の総損失率を取得する。<p>
     *
     * @return 保有中取引の総損失率
     */
    public double getTotalLossRatioInHolding(){
        return totalLossRatioInHolding;
    }
    
    /**
     * 保有中取引を含む総損失率を取得する。<p>
     *
     * @return 保有中取引を含む総損失率
     */
    public double getTotalLossRatioWithHolding(){
        return totalLossRatio + getTotalLossRatioInHolding();
    }
    
    /**
     * 平均損失率を取得する。<p>
     *
     * @return 平均損失率
     */
    public double getAverageLossRatio(){
        final int lossTradeNum = tradeNum - winTradeNum;
        return lossTradeNum == 0 ? 0d : totalLossRatio / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引の平均損失率を取得する。<p>
     *
     * @return 保有中取引の平均損失率
     */
    public double getAverageLossRatioInHolding(){
        final int lossTradeNum = getTradeNumInHolding() - getWinTradeNumInHolding();
        return lossTradeNum == 0 ? 0d : getTotalLossRatioInHolding() / (double)lossTradeNum;
    }
    
    /**
     * 保有中取引を含む平均損失率を取得する。<p>
     *
     * @return 保有中取引を含む平均損失率
     */
    public double getAverageLossRatioWithHolding(){
        final int lossTradeNum = getTradeNumWithHolding() - getWinTradeNumWithHolding();
        return lossTradeNum == 0 ? 0d : (getTotalLossRatioWithHolding() / (double)lossTradeNum);
    }
    
    /**
     * 最大損失率を取得する。<p>
     *
     * @return 最大損失率
     */
    public double getMaxLossRatio(){
        return maxLossRatio;
    }
    
    /**
     * 保有中取引の最大損失率を取得する。<p>
     *
     * @return 保有中取引の最大損失率
     */
    public double getMaxLossRatioInHolding(){
        return maxLossRatioInHolding;
    }
    
    /**
     * 保有中取引を含む最大損失率を取得する。<p>
     *
     * @return 保有中取引を含む最大損失率
     */
    public double getMaxLossRatioWithHolding(){
        return Math.max(maxLossRatio, getMaxLossRatioInHolding());
    }
    
    /**
     * 総損益を取得する。<p>
     *
     * @return 総損益
     */
    public double getTotalProfitAndLoss(){
        return totalProfit + totalLoss;
    }
    
    /**
     * 保有中取引の総損益を取得する。<p>
     *
     * @return 保有中取引の総損益
     */
    public double getTotalProfitAndLossInHolding(){
        return getTotalProfitInHolding() + getTotalLossInHolding();
    }
    
    /**
     * 保有中取引を含む総損益を取得する。<p>
     *
     * @return 保有中取引を含む総損益
     */
    public double getTotalProfitAndLossWithHolding(){
        return getTotalProfitAndLoss() + getTotalProfitAndLossInHolding();
    }
    
    /**
     * 平均損益を取得する。<p>
     *
     * @return 平均損益
     */
    public double getAverageProfitAndLoss(){
        return tradeNum == 0 ? 0d : (getTotalProfitAndLoss() / (double)tradeNum);
    }
    
    /**
     * 保有中取引の平均損益を取得する。<p>
     *
     * @return 保有中取引の平均損益
     */
    public double getAverageProfitAndLossInHolding(){
        return getTradeNumInHolding() == 0 ? 0d : (getTotalProfitAndLossInHolding() / (double)getTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均損益を取得する。<p>
     *
     * @return 保有中取引を含む平均損益
     */
    public double getAverageProfitAndLossWithHolding(){
        return getTradeNumWithHolding() == 0 ? 0d : (getTotalProfitAndLossWithHolding() / (double)getTradeNumWithHolding());
    }
    
    /**
     * 総損益率を取得する。<p>
     *
     * @return 総損益率
     */
    public double getTotalProfitAndLossRatio(){
        return totalProfitRatio + totalLossRatio;
    }
    
    /**
     * 保有中取引の総損益率を取得する。<p>
     *
     * @return 保有中取引の総損益率
     */
    public double getTotalProfitAndLossRatioInHolding(){
        return getTotalProfitRatioInHolding() + getTotalLossRatioInHolding();
    }
    
    /**
     * 保有中取引を含む総損益率を取得する。<p>
     *
     * @return 保有中取引を含む総損益率
     */
    public double getTotalProfitAndLossRatioWithHolding(){
        return getTotalProfitAndLossRatio() + getTotalProfitAndLossRatioInHolding();
    }
    
    /**
     * 平均損益率を取得する。<p>
     *
     * @return 平均損益率
     */
    public double getAverageProfitAndLossRatio(){
        return tradeNum == 0 ? 0d : (getTotalProfitAndLossRatio() / (double)tradeNum);
    }
    
    /**
     * 保有中取引の平均損益率を取得する。<p>
     *
     * @return 保有中取引の平均損益率
     */
    public double getAverageProfitAndLossRatioInHolding(){
        return getTradeNumInHolding() == 0 ? 0d : (getTotalProfitAndLossRatioInHolding() / (double)getTradeNumInHolding());
    }
    
    /**
     * 保有中取引を含む平均損率益を取得する。<p>
     *
     * @return 保有中取引を含む平均損益率
     */
    public double getAverageProfitAndLossRatioWithHolding(){
        return getTradeNumWithHolding() == 0 ? 0d : (getTotalProfitAndLossRatioWithHolding() / (double)getTradeNumWithHolding());
    }
    
    /**
     * 損益要因を取得する。<p>
     *
     * @return 損益要因
     */
    public double getProfitFactor(){
        return totalLoss == 0d ? 0d : Math.abs(totalProfit / totalLoss);
    }
    
    /**
     * 保有中取引の損益要因を取得する。<p>
     *
     * @return 保有中取引の損益要因
     */
    public double getProfitFactorInHolding(){
        return getTotalLossInHolding() == 0d ? 0d : Math.abs(getTotalProfitInHolding() / getTotalLossInHolding());
    }
    
    /**
     * 保有中取引を含む損益要因を取得する。<p>
     *
     * @return 保有中取引を含む損益要因
     */
    public double getProfitFactorWithHolding(){
        return getTotalLossWithHolding() == 0d ? 0d : Math.abs(getTotalProfitWithHolding() / getTotalLossWithHolding());
    }
    
    /**
     * 取引回数を取得する。<p>
     *
     * @return 取引回数
     */
    public int getTradeNum(){
        return tradeNum;
    }
    
    /**
     * 保有中取引の取引回数を取得する。<p>
     *
     * @return 保有中取引の取引回数
     */
    public int getTradeNumInHolding(){
        return tradeNumInHolding;
    }
    
    /**
     * 保有中取引を含む取引回数を取得する。<p>
     *
     * @return 保有中取引を含む取引回数
     */
    public int getTradeNumWithHolding(){
        return tradeNum + getTradeNumInHolding();
    }
    
    /**
     * 勝ち取引回数を取得する。<p>
     *
     * @return 勝ち取引回数
     */
    public int getWinTradeNum(){
        return winTradeNum;
    }
    
    /**
     * 保有中取引の勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引の勝ち取引回数
     */
    public int getWinTradeNumInHolding(){
        return winTradeNumInHolding;
    }
    
    /**
     * 保有中取引を含む勝ち取引回数を取得する。<p>
     *
     * @return 保有中取引を含む勝ち取引回数
     */
    public int getWinTradeNumWithHolding(){
        return winTradeNum + getWinTradeNumInHolding();
    }
    
    /**
     * 勝率を取得する。<p>
     *
     * @return 勝率
     */
    public float getWinTradeRatio(){
        return tradeNum == 0 ? 0f : (float)winTradeNum / (float)tradeNum;
    }
    
    /**
     * 保有中取引の勝率を取得する。<p>
     *
     * @return 保有中取引の勝率
     */
    public float getWinTradeRatioInHolding(){
        return getTradeNumInHolding() == 0 ? 0f : (float)getWinTradeNumInHolding() / (float)getTradeNumInHolding();
    }
    
    /**
     * 保有中取引を含む勝率を取得する。<p>
     *
     * @return 保有中取引を含む勝率
     */
    public float getWinTradeRatioWithHolding(){
        return getTradeNumWithHolding() == 0 ? 0f : (float)(winTradeNum + getWinTradeNumInHolding()) / (float)getTradeNumWithHolding();
    }
    
    /**
     * 負け取引回数を取得する。<p>
     *
     * @return 負け取引回数
     */
    public int getLoseTradeNum(){
        return tradeNum - winTradeNum;
    }
    
    /**
     * 保有中取引の負け取引回数を取得する。<p>
     *
     * @return 保有中取引の負け取引回数
     */
    public int getLoseTradeNumInHolding(){
        return getTradeNumInHolding() - getWinTradeNumInHolding();
    }
    
    /**
     * 保有中取引を含む負け取引回数を取得する。<p>
     *
     * @return 保有中取引を含む負け取引回数
     */
    public int getLoseTradeNumWithHolding(){
        return (getTradeNumWithHolding()) - (winTradeNum + getWinTradeNumInHolding());
    }
    
    /**
     * 取引対象開始日時。<p>
     *
     * @return 取引対象開始日時
     */
    public Date getTradeTargetStartTime(){
        return tradeTargetStartTime;
    }
    
    /**
     * 取引対象終了日時。<p>
     *
     * @return 取引対象終了日時
     */
    public Date getTradeTargetEndTime(){
        return tradeTargetEndTime;
    }
    
    /**
     * 取引対象期間。<p>
     *
     * @param unitMillis 単位時間[ms]
     * @return 取引対象期間
     */
    public double getTradeTargetTerm(long unitMillis){
        if(tradeTargetStartTime == null || tradeTargetEndTime == null){
            return 0d;
        }
        return (double)(tradeTargetEndTime.getTime() - tradeTargetStartTime.getTime()) / (double)unitMillis;
    }
    
    /**
     * 取引率。<p>
     *
     * @return 取引率
     */
    public float getTradeRatio(){
        return (float)((double)totalHoldingTermMillis / getTradeTargetTerm(1l));
    }
    
    /**
     * 保有中取引を含む取引率。<p>
     *
     * @return 保有中取引を含む取引率
     */
    public float getTradeRatioWithHolding(){
        return (float)((double)(totalHoldingTermMillis + totalHoldingTermMillisInHolding) / getTradeTargetTerm(1l));
    }
    
    /**
     * 精算率。<p>
     *
     * @return 精算率
     */
    public double getPayOffRatio(){
        return getAverageLoss() == 0 ? 0d : Math.abs(getAverageProfit() / getAverageLoss());
    }
    
    /**
     * 保有中取引の精算率。<p>
     *
     * @return 保有中取引の精算率
     */
    public double getPayOffRatioInHolding(){
        return getAverageLossInHolding() == 0 ? 0d : Math.abs(getAverageProfitInHolding() / getAverageLossInHolding());
    }
    
    /**
     * 保有中取引を含む精算率。<p>
     *
     * @return 保有中取引を含む精算率
     */
    public double getPayOffRatioWithHolding(){
        return getAverageLossWithHolding() == 0 ? 0d : Math.abs(getAverageProfitWithHolding() / getAverageLossWithHolding());
    }
    
    /**
     * 計算する。<p>
     */
    public abstract void calculate();
    
    /**
     * 時系列データに沿って計算する。<p>
     * 
     * @return 現在の時系列要素。時系列データの最後まで計算されている場合は、null
     */
    public abstract <E extends TimeSeries.Element> E calculateByTimeSeries();
    
    public void clear(){
        totalProfit = 0.0d;
        totalProfitInHolding = 0.0d;
        totalProfitRatio = 0.0d;
        totalProfitRatioInHolding = 0.0d;
        totalLoss = 0.0d;
        totalLossInHolding = 0.0d;
        totalLossRatio = 0.0d;
        totalLossRatioInHolding = 0.0d;
        tradeNum = 0;
        tradeNumInHolding = 0;
        winTradeNum = 0;
        winTradeNumInHolding = 0;
        maxProfit = 0d;
        maxProfitInHolding = 0d;
        maxLoss = 0d;
        maxLossInHolding = 0d;
        maxProfitRatio = 0d;
        maxProfitRatioInHolding = 0d;
        maxLossRatio = 0d;
        maxLossRatioInHolding = 0d;
        tradeTargetStartTime = null;
        tradeTargetEndTime = null;
        totalHoldingTermMillis = 0;
        totalHoldingTermMillisInHolding = 0;
    }
    
    public String toString(){
        final StringBuilder buf = new StringBuilder(super.toString());
        buf.append('{');
        buf.append("totalProfit=").append(getTotalProfit());
        buf.append(", totalProfitInHolding=").append(getTotalProfitInHolding());
        buf.append(", totalProfitWithHolding=").append(getTotalProfitWithHolding());
        buf.append(", averageProfit=").append(getAverageProfit());
        buf.append(", averageProfitInHolding=").append(getAverageProfitInHolding());
        buf.append(", averageProfitWithHolding=").append(getAverageProfitWithHolding());
        buf.append(", maxProfit=").append(getMaxProfit());
        buf.append(", maxProfitInHolding=").append(getMaxProfitInHolding());
        buf.append(", maxProfitWithHolding=").append(getMaxProfitWithHolding());
        buf.append(", totalProfitRatio=").append(getTotalProfitRatio());
        buf.append(", totalProfitRatioInHolding=").append(getTotalProfitRatioInHolding());
        buf.append(", totalProfitRatioWithHolding=").append(getTotalProfitRatioWithHolding());
        buf.append(", averageProfitRatio=").append(getAverageProfitRatio());
        buf.append(", averageProfitRatioInHolding=").append(getAverageProfitRatioInHolding());
        buf.append(", averageProfitRatioWithHolding=").append(getAverageProfitRatioWithHolding());
        buf.append(", maxProfitRatio=").append(getMaxProfitRatio());
        buf.append(", maxProfitRatioInHolding=").append(getMaxProfitRatioInHolding());
        buf.append(", maxProfitRatioWithHolding=").append(getMaxProfitRatioWithHolding());
        buf.append(", totalLoss=").append(getTotalLoss());
        buf.append(", totalLossInHolding=").append(getTotalLossInHolding());
        buf.append(", totalLossWithHolding=").append(getTotalLossWithHolding());
        buf.append(", averageLoss=").append(getAverageLoss());
        buf.append(", averageLossInHolding=").append(getAverageLossInHolding());
        buf.append(", averageLossWithHolding=").append(getAverageLossWithHolding());
        buf.append(", maxLoss=").append(getMaxLoss());
        buf.append(", maxLossInHolding=").append(getMaxLossInHolding());
        buf.append(", maxLossWithHolding=").append(getMaxLossWithHolding());
        buf.append(", totalLossRatio=").append(getTotalLossRatio());
        buf.append(", totalLossRatioInHolding=").append(getTotalLossRatioInHolding());
        buf.append(", totalLossRatioWithHolding=").append(getTotalLossRatioWithHolding());
        buf.append(", averageLossRatio=").append(getAverageLossRatio());
        buf.append(", averageLossRatioInHolding=").append(getAverageLossRatioInHolding());
        buf.append(", averageLossRatioWithHolding=").append(getAverageLossRatioWithHolding());
        buf.append(", maxLossRatio=").append(getMaxLossRatio());
        buf.append(", maxLossRatioInHolding=").append(getMaxLossRatioInHolding());
        buf.append(", maxLossRatioWithHolding=").append(getMaxLossRatioWithHolding());
        buf.append(", totalProfitAndLoss=").append(getTotalProfitAndLoss());
        buf.append(", totalProfitAndLossInHolding=").append(getTotalProfitAndLossInHolding());
        buf.append(", totalProfitAndLossInHolding=").append(getTotalProfitAndLossWithHolding());
        buf.append(", averageProfitAndLoss=").append(getAverageProfitAndLoss());
        buf.append(", averageProfitAndLossInHolding=").append(getAverageProfitAndLossInHolding());
        buf.append(", averageProfitAndLossWithHolding=").append(getAverageProfitAndLossWithHolding());
        buf.append(", totalProfitAndLossRatio=").append(getTotalProfitAndLossRatio());
        buf.append(", totalProfitAndLossRatioInHolding=").append(getTotalProfitAndLossRatioInHolding());
        buf.append(", totalProfitAndLossRatio=").append(getTotalProfitAndLossRatioWithHolding());
        buf.append(", averageProfitAndLossRatio=").append(getAverageProfitAndLossRatio());
        buf.append(", averageProfitAndLossRatioInHolding=").append(getAverageProfitAndLossRatioInHolding());
        buf.append(", averageProfitAndLossRatioWithHolding=").append(getAverageProfitAndLossRatioWithHolding());
        buf.append(", profitFactor=").append(getProfitFactor());
        buf.append(", profitFactorInHolding=").append(getProfitFactorInHolding());
        buf.append(", profitFactorWithHolding=").append(getProfitFactorWithHolding());
        buf.append(", tradeNum=").append(getTradeNum());
        buf.append(", tradeNumInHolding=").append(getTradeNumInHolding());
        buf.append(", tradeNumWithHolding=").append(getTradeNumWithHolding());
        buf.append(", winTradeNum=").append(getWinTradeNum());
        buf.append(", winTradeNumInHolding=").append(getWinTradeNumInHolding());
        buf.append(", winTradeNumWithHolding=").append(getWinTradeNumWithHolding());
        buf.append(", winTradeRatio=").append(getWinTradeRatio());
        buf.append(", winTradeRatioInHolding=").append(getWinTradeRatioInHolding());
        buf.append(", winTradeRatioWithHolding=").append(getWinTradeRatioWithHolding());
        buf.append(", loseTradeNum=").append(getLoseTradeNum());
        buf.append(", loseTradeNumInHolding=").append(getLoseTradeNumInHolding());
        buf.append(", loseTradeNumWithHolding=").append(getLoseTradeNumWithHolding());
        buf.append(", tradeTargetStartTime=").append(getTradeTargetStartTime());
        buf.append(", tradeTargetEndTime=").append(getTradeTargetEndTime());
        buf.append(", tradeRatio=").append(getTradeRatio());
        buf.append(", tradeRatioWithHolding=").append(getTradeRatioWithHolding());
        buf.append(", payOffRatio=").append(getPayOffRatio());
        buf.append(", payOffRatioInHolding=").append(getPayOffRatioInHolding());
        buf.append(", payOffRatioWithHolding=").append(getPayOffRatioWithHolding());
        buf.append('}');
        return buf.toString();
    }
}