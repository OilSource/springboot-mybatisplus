package com.mybatis.plus.map;


import com.mybatis.plus.UserDto;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CachingMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private static final long DEFAULT_EXPIRY = 1000 * 60 * 15;
    /**
     * 分隔符
     */
    private static final String SEPERATOR = "------";
    /**
     * username的key模板
     */
    private static final String USERNAME_KEY_STR ="-username-";
    /**
     * password的key模板
     */
    private static final String USERPASSWORD_KEY_STR ="-userpassword-";
    /**
     * userip的key模板
     */
    private static final String USERIP_KEY_STR ="-userip-";
    /**
     * email的key模板
     */
    private static final String EMAIL_KEY_STR ="-email-";
    /**
     * groupId模板
     */
    private static final String GROUP_ID_KEY_STR ="groupid-";

    /**
     *  失效时间，单位毫秒
     */
    private long expiry ;

    /**
     * 记录每条记录的失效时间
     */
    private Map<Long, List<String>> expiryMap ;

    public CachingMap(long defaultExpiryTime){
        this(1 << 4, defaultExpiryTime);
    }

    public CachingMap(int initialCapacity){
        this(initialCapacity, DEFAULT_EXPIRY);
    }

    public CachingMap(int initialCapacity, long defaultExpiryTime){
        //每一条会对应hashMap里的5条记录
        super(initialCapacity*5);
        this.expiry = defaultExpiryTime<=0?DEFAULT_EXPIRY:defaultExpiryTime;
        expiryMap= new TreeMap<Long, List<String>>();
    }

    /**
     * 禁止put,putall方法，统一调用putData方法
     * @param key
     * @param value
     * @return
     */
    @Override
    public Object put(String key, Object value) {
        return null;
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
    }

    /**
     * 将每一条采集记录，封装到CachingMap中
     * @param userDto
     */
    public void putObjectData(UserDto userDto){
        String uuidAndGroupId = userDto.getGroupId().concat(SEPERATOR).concat(userDto.getUuid());
        long expiryTime = System.currentTimeMillis()+expiry;
        String[] arr = new String[]{
                userDto.getPhraseId(), userDto.getUsername(), userDto.getPassword()
                , userDto.getIp(), userDto.getEmail(),expiryTime+""};
        putData(USERNAME_KEY_STR, userDto.getPhraseId(), userDto.getUsername(),uuidAndGroupId);
        putData(USERPASSWORD_KEY_STR, userDto.getPhraseId(), userDto.getPassword(),uuidAndGroupId);
        putData(USERIP_KEY_STR, userDto.getPhraseId(), userDto.getIp(),uuidAndGroupId);
        putData(EMAIL_KEY_STR, userDto.getPhraseId(), userDto.getEmail(),uuidAndGroupId);
        super.put(GROUP_ID_KEY_STR.concat(uuidAndGroupId),arr);
        List<String> groupIds =expiryMap.get(expiryTime);
        if(null==groupIds){
            groupIds = new ArrayList<>();
            groupIds.add(uuidAndGroupId);
            expiryMap.put(expiryTime, groupIds);
        } else{
            groupIds.add(uuidAndGroupId);
        }

    }

    /**
     * 为User添加GroupId
     * @param userDto
     */
    public void setGroupId(UserDto userDto, Info info){
        String groupId = getData(USERNAME_KEY_STR, userDto.getPhraseId(), userDto.getUsername());
        if(StringUtils.isEmpty(groupId)){
            groupId = getData(USERPASSWORD_KEY_STR, userDto.getPhraseId(), userDto.getPassword());
            if(StringUtils.isEmpty(groupId)){
                groupId = getData(USERIP_KEY_STR, userDto.getPhraseId(), userDto.getIp());
                if(StringUtils.isEmpty(groupId)){
                    groupId = getData(EMAIL_KEY_STR, userDto.getPhraseId(), userDto.getEmail());
                }
            }
        }
        if(StringUtils.isEmpty(groupId)){
            userDto.setGroupId(""+info.getMaxGroupId());
        } else {
            userDto.setGroupId(groupId.split(SEPERATOR)[0]);
        }

        //加入缓存
        putObjectData(userDto);
    }
    


    /**
     * 获取记录数
     */
    public int getDataSize(){
        return expiryMap.size();
    }

    /**
     * 清理无效过期的数据
     */
    public void clearInvalidData(){
        Iterator<Entry<Long,List<String>>> it =  expiryMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Long,List<String>> entry = it.next();
            if(System.currentTimeMillis()>entry.getKey()){
                List<String> uuidAndGroupIdList = expiryMap.get(entry.getValue());
                for(String uuidAndGroupId:uuidAndGroupIdList){
                    String [] arr = (String[])super.get(uuidAndGroupId);
                    super.remove(generateKey(USERNAME_KEY_STR,arr[0],arr[1]));
                    super.remove(generateKey(USERPASSWORD_KEY_STR,arr[0],arr[2]));
                    super.remove(generateKey(USERIP_KEY_STR,arr[0],arr[3]));
                    super.remove(generateKey(EMAIL_KEY_STR,arr[0],arr[4]));
                    super.remove(entry.getKey());
                }
                expiryMap.remove(entry.getKey());
            } else{
                break;
            }
        }
    }

    private String generateKey(final String templateStr,String phraseId,String mark){
        return phraseId.concat(templateStr).concat(mark);
    }

    private void putData(final String templateStr,String phraseId,String mark,String uuidAndGroupId){
        if(StringUtils.isEmpty(mark)){
            return;
        }
       super.put(generateKey(templateStr,phraseId,mark),uuidAndGroupId);
    }

    private String getData(final String templateStr,String phraseId,String mark){
        if(StringUtils.isEmpty(mark)){
            return null;
        }
        return  (String)super.get(generateKey(templateStr,phraseId,mark));
    }

    public Map<Long, List<String>> getExpiryMap() {
        return expiryMap;
    }
}
