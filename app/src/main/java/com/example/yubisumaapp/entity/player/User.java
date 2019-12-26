package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.GameMaster;
import com.example.yubisumaapp.entity.motion.skill.Skill;

import java.util.ArrayList;

public class User extends Player {

    private String comment="", voice="";
    private int eventID=-1;

    public User(int skillPoint, int fingerStock, int playerIndex) {
        super(skillPoint, fingerStock, playerIndex);
    }

    public String[] getAvailableSkillNameArray() {
        // スキルリストをスイッチ
        ArrayList<Skill> skillList = getAvailableSkillList();
        // スキルネームリスト作成
        ArrayList<String> skillNameList = new ArrayList<>();
        for(Skill skill : skillList) {
            skillNameList.add(skill.getSkillName());
        }
        // 配列に変換
        String[] activeSkillArray = new String[skillNameList.size()];
        for(int index=0; index<skillList.size(); index++) {
            activeSkillArray[index] = skillNameList.get(index);
        }
        return activeSkillArray;
    }

    @Override
    public void turnEnd() {
        setSilent();
        super.turnEnd();
    }

    public void setComment(String comment) {
        if(this.comment.equals("")) {
            this.comment = comment;
        }
    }

    public void setVoice(String voice) {
        if (this.voice.equals("")){
            this.voice = voice;
        }
    }

    public void setEventID(int eventID) {
        if (this.eventID == GameMaster.NOT_EVENT) {
            this.eventID = eventID;
        }
    }

    public void setSilent() {
        comment = "";
        voice = "";
        eventID = GameMaster.NOT_EVENT;
    }

    public String getComment() { return comment; }

    public String getVoice() { return voice; }

    public int getEventID() { return eventID; }



}
