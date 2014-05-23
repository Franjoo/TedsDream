package com.angrynerds.tedsdream.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class MyButton extends Button{

    private Integer id;
	public static int overCounter = 0;
	private boolean over = false;
	
	public MyButton(int id, Drawable up, Drawable down) {
        super(up, down, down);
        this.id = new Integer(id);
	}
	
	public void setOver(boolean b){
		this.over = b;
	}
	
	public Boolean getOver(){
		return over;
	}
	
    public Integer getId(){
        return id;
    }

}
