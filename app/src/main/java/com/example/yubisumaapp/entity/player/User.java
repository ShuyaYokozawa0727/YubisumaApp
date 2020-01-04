package com.example.yubisumaapp.entity.player;

import com.example.yubisumaapp.entity.GameMaster;
import com.example.yubisumaapp.entity.motion.Action;
import com.example.yubisumaapp.entity.motion.Call;
import com.example.yubisumaapp.entity.motion.skill.Skill;
import com.example.yubisumaapp.realm.RealmHelper;

import java.util.ArrayList;

public class User extends Player {

    private String comment="", voice="";
    private int eventID=-1;
    public int startScore;
    private int turnScore;
    private RealmHelper realmHelper;

    public User(int skillPoint, int fingerStock, int playerIndex) {
        super(skillPoint, fingerStock, playerIndex);
        realmHelper = new RealmHelper();
        startScore = turnScore = realmHelper.gameData.getScore();
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
        // このターンのスコアとしてキャッシュ
        turnScore = realmHelper.gameData.getScore();
        super.turnEnd();
    }

    public void setScore(int score) {
        realmHelper.realm.beginTransaction();
        realmHelper.gameData.setScore(realmHelper.gameData.getScore()+score);
        realmHelper.realm.commitTransaction();
    }

    public int getScore() {
        return realmHelper.gameData.getScore();
    }

    public int getDiffGameScore() {
        return realmHelper.gameData.getScore() - startScore;
    }

    public int getDiffTurnScore() { return realmHelper.gameData.getScore() - turnScore;}

    public void addAction(Action action) {
        if (motion == null) {
            // 子だったらActionだけセット
            setMotion(action);
        } else if(motion instanceof Call) {
            // CallしてたらActionを組み込む
            getCall().setAction(action);
        }
        // スキルなら無視
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
