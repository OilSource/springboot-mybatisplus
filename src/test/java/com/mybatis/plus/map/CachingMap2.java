package com.mybatis.plus.map;


import com.mybatis.plus.UserDto;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CachingMap2 extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private static final long DEFAULT_EXPIRY = 1000 * 60 * 15;
    /**
     * 分隔符
     */
    private static final String SEPERATOR = "------";
    /**
     * username的key模板
     */
    private HashMap<String, String> userNameMap ;
    /**
     * password的key模板
     */
    private HashMap<String, String> passwordMap ;
    /**
     * userip的key模板
     */
    private HashMap<String, String> userIpMap ;
    /**
     * email的key模板
     */
    private HashMap<String, String> emailMap ;
    /**
     * groupId模板
     */
    private HashMap<String, Object> groupMap ;

    /**
     *  失效时间，单位毫秒
     */
    private long expiry ;

    /**
     * 记录每条记录的失效时间
     */
    private HashMap<String, Long> expiryMap ;

    public CachingMap2(long defaultExpiryTime){
        this(1 << 4, defaultExpiryTime);
    }

    public CachingMap2(int initialCapacity){
        this(initialCapacity, DEFAULT_EXPIRY);
    }

    public CachingMap2(int initialCapacity, long defaultExpiryTime){
        //每一条会对应hashMap里的5条记录
        super(initialCapacity);
        this.expiry = defaultExpiryTime<=0?DEFAULT_EXPIRY:defaultExpiryTime;
        expiryMap= new HashMap<>(initialCapacity);
        userNameMap= new HashMap<>(initialCapacity);
        passwordMap= new HashMap<>(initialCapacity);
        userIpMap= new HashMap<>(initialCapacity);
        emailMap= new HashMap<>(initialCapacity);
        groupMap= new HashMap<>(initialCapacity);
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
        String[] arr = new String[]{userDto.getPhraseId(), userDto.getUsername(), userDto.getPassword(), userDto.getIp(), userDto.getEmail()};
        userNameMap.put(userDto.getPhraseId()+ userDto.getUsername(),uuidAndGroupId);
        passwordMap.put(userDto.getPhraseId()+ userDto.getPassword(),uuidAndGroupId);
        userIpMap.put(userDto.getPhraseId()+ userDto.getIp(),uuidAndGroupId);
        emailMap.put(userDto.getPhraseId()+ userDto.getEmail(),uuidAndGroupId);
        groupMap.put(uuidAndGroupId,arr);
        expiryMap.put(uuidAndGroupId,System.currentTimeMillis()+expiry);
    }

    /**
     * 为User添加GroupId
     * @param userDto
     */
    public void setGroupId(UserDto userDto, Info info){
        String groupId = userNameMap.get(userDto.getPhraseId()+ userDto.getUsername());
        if(StringUtils.isEmpty(groupId)){
            groupId = passwordMap.get(userDto.getPhraseId()+ userDto.getPassword());
            if(StringUtils.isEmpty(groupId)){
                groupId = userIpMap.get(userDto.getPhraseId()+ userDto.getIp());
                if(StringUtils.isEmpty(groupId)){
                    groupId = emailMap.get(userDto.getPhraseId()+ userDto.getEmail());
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
//        Iterator<Entry<String, Long>> it =  expiryMap.entrySet().iterator();
//        while(it.hasNext()){
//            Entry<String,Long> entry = it.next();
//            if(System.currentTimeMillis()>entry.getValue()){
//                String [] arr = (String[])super.get(entry.getKey());
//                super.remove(generateKey(USERNAME_KEY_STR,arr[0],arr[1]));
//                super.remove(generateKey(USERPASSWORD_KEY_STR,arr[0],arr[2]));
//                super.remove(generateKey(USERIP_KEY_STR,arr[0],arr[3]));
//                super.remove(generateKey(EMAIL_KEY_STR,arr[0],arr[4]));
//                super.remove(entry.getKey());
//                expiryMap.remove(entry.getKey());
//            }
//        }
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

}
