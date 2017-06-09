/*
 * The MIT License
 *
 * Copyright 2017 KSat e.V.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ksatstuttgart.usoc.data.sensors.chartData;

import com.ksatstuttgart.usoc.data.sensors.Mode;
import com.ksatstuttgart.usoc.data.sensors.SensorPos;
import com.ksatstuttgart.usoc.data.sensors.State;
import com.ksatstuttgart.usoc.controller.Utility;
import java.awt.Point;


/**
* <h1>CJ</h1>
* This class is a data wrapper for the ColdJunction chips on the MIRKA2-RX mission.
* 
* <p>
* 
*
* @author  Valentin Starlinger
* @version 1.0
*/
public class ColdjunctionChart extends ChartSensor{
    
    private State state;
    
    private int temp;
    private int num,tpos, systime;
    private Mode m;

    
    public ColdjunctionChart(String content, int tpos, int num, Mode mode, int systime) {
        this.tpos = tpos;
        this.num = num;
        this.m = mode;
        this.systime = systime;
        parseData(content);
    }
    
    @Override
    public Double getTime() {
        return new Double((systime + (tpos - 1)*(12/SensorPos.CJ.getFrequency(m))));
    }

    @Override
    public Double getYValue() {
        return ((double)temp)/10;
    }
        
    public void parseData(String content) {
        this.state = State.getState(Integer.parseUnsignedInt(content.substring(SensorPos.CJ_HK.getStart(m)+(num-1), SensorPos.CJ_HK.getEnd(m)+(num-1)),2));
        
        int diff = SensorPos.CJ.getDiff(m);
        int timeDiff = diff * 6;
        
        this.temp = Utility.binToInt(content.substring(SensorPos.CJ.getStart(m)+(num-1)*diff+(tpos-1)*timeDiff
                , SensorPos.CJ.getEnd(m)+(num-1)*diff+(tpos-1)*timeDiff));
        
    }
    
    @Override
    public String toString(){
        return "Coldjunction "+num+": "+state+", temp: "+getYValue()+" at tpos: "+tpos;
    }

    public State getState() {
        return state;
    }

    public int getTemp() {
        return temp;
    }
}