package com.mybatis.plus.cache;


import com.mybatis.plus.dto.Info;
import com.mybatis.plus.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
@Slf4j
public class CachingMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    private static final long DEFAULT_EXPIRY = 1000 * 6 * 15;
    /**
     * 分隔符
     */
    private static final String SEPERATOR = "------";
    /**
     * username的key模板
     */
    private static final String USERNAME_KEY_STR = "-username-";
    /**
     * password的key模板
     */
    private static final String USERPASSWORD_KEY_STR = "-userpassword-";
    /**
     * userip的key模板
     */
    private static final String USERIP_KEY_STR = "-userip-";
    /**
     * email的key模板
     */
    private static final String EMAIL_KEY_STR = "-email-";
    /**
     * groupId模板
     */
    private static final String GROUP_ID_KEY_STR = "groupid-";

    /**
     * 失效时间，单位毫秒
     */
    private long expiry;

    /**
     * 记录每条记录的失效时间
     */
    private Map<Long, List<String>> expiryMap;
    /**
     * 缓存待刷新的key
     */
    private Set<String> tempKeys =new HashSet<>();

    public CachingMap(long defaultExpiryTime) {
        this(1 << 4, defaultExpiryTime);
    }

    public CachingMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_EXPIRY);
    }

    public CachingMap(int initialCapacity, long defaultExpiryTime) {
        //每一条会对应hashMap里的5条记录
        super(initialCapacity * 5);
        this.expiry = defaultExpiryTime <= 0 ? DEFAULT_EXPIRY : defaultExpiryTime;
        expiryMap = new TreeMap<Long, List<String>>();
    }

    /**
     * 禁止put,putall方法，统一调用putData方法
     *
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

    public void setAllGroupId(List<UserDto> userDtoList,Info info){
        long start =System.currentTimeMillis();
        for(UserDto userDto: userDtoList){
            setGroupId(userDto,info);
        }
        log.info("设置属性执行时间："+(System.currentTimeMillis()-start));
    }

    public void putAllObjectData(List<UserDto> userDtoList){
        long start =System.currentTimeMillis();
        for(UserDto userDto: userDtoList){
            putObjectData(userDto);
        }
        log.info("封装数据执行时间："+(System.currentTimeMillis()-start));
    }

    /**
     * 将每一条采集记录，封装到CachingMap中
     *
     * @param userDto
     */
    public void putObjectData(UserDto userDto) {
        String uuidAndGroupId = userDto.getGroupId().concat(SEPERATOR).concat(userDto.getUuid());
        long expiryTime = System.currentTimeMillis() + expiry;
        String[] arr = new String[]{
                userDto.getPhraseId(), userDto.getUsername(), userDto.getPassword()
                , userDto.getIp(), userDto.getEmail(), expiryTime + ""};
        putData(USERNAME_KEY_STR, userDto.getPhraseId(), userDto.getUsername(), uuidAndGroupId);
        putData(USERPASSWORD_KEY_STR, userDto.getPhraseId(), userDto.getPassword(), uuidAndGroupId);
        putData(USERIP_KEY_STR, userDto.getPhraseId(), userDto.getIp(), uuidAndGroupId);
        putData(EMAIL_KEY_STR, userDto.getPhraseId(), userDto.getEmail(), uuidAndGroupId);
        super.put(GROUP_ID_KEY_STR.concat(uuidAndGroupId), arr);
        addExpiryData(uuidAndGroupId, expiryTime);
    }

    private void addExpiryData(String uuidAndGroupId, long expiryTime) {
        List<String> groupIds = expiryMap.get(expiryTime);
        if (null == groupIds) {
            groupIds = new ArrayList<>();
            groupIds.add(uuidAndGroupId);
            expiryMap.put(expiryTime, groupIds);
        } else {
            groupIds.add(uuidAndGroupId);
        }
    }

    /**
     * 为User添加GroupId
     *
     * @param userDto
     */
    public void setGroupId(UserDto userDto, Info info) {
        String uuidAndGroupId = getData(USERNAME_KEY_STR, userDto.getPhraseId(), userDto.getUsername());
        if (StringUtils.isEmpty(uuidAndGroupId)) {
            uuidAndGroupId = getData(USERPASSWORD_KEY_STR, userDto.getPhraseId(), userDto.getPassword());
            if (StringUtils.isEmpty(uuidAndGroupId)) {
                uuidAndGroupId = getData(USERIP_KEY_STR, userDto.getPhraseId(), userDto.getIp());
                if (StringUtils.isEmpty(uuidAndGroupId)) {
                    uuidAndGroupId = getData(EMAIL_KEY_STR, userDto.getPhraseId(), userDto.getEmail());
                }
            }
        }
        if (StringUtils.isEmpty(uuidAndGroupId)) {
            userDto.setGroupId("" + info.getMaxGroupId());
        } else {
            //将需要刷新过期时间的key加入缓存Set集合中，在做失效的时候一起处理
            tempKeys.add(uuidAndGroupId);
            userDto.setGroupId(uuidAndGroupId.split(SEPERATOR)[0]);
        }
        //加入缓存
        putObjectData(userDto);
    }

    public void refreshDateTime(){
        if(!CollectionUtils.isEmpty(tempKeys)){
            long newExpiryTime = System.currentTimeMillis()+expiry;
            Iterator<String> it =tempKeys.iterator();
            while(it.hasNext()){
                String uuidAndGroupId =it.next();
                String[] arr = (String[])super.get(GROUP_ID_KEY_STR.concat(uuidAndGroupId));
                long oldExpiryTime =  Long.valueOf(arr[arr.length-1]);
                arr[arr.length-1] = ""+newExpiryTime;
                //删除expiryMap中的老数据
                List<String> uuidAndGroupIdList =expiryMap.get(oldExpiryTime);
                if(uuidAndGroupIdList.size() ==1){
                    expiryMap.remove(oldExpiryTime);
                } else{
                    uuidAndGroupIdList.remove(uuidAndGroupId);
                }
                //新增新的数据
                addExpiryData(uuidAndGroupId,newExpiryTime);
                it.remove();
            }
        }
    }


    /**
     * 获取记录数
     */
    public int getDataSize() {
        return expiryMap.size();
    }

    /**
     * 清理无效过期的数据
     */
    public void clearInvalidData() {
        long start =System.currentTimeMillis();
        refreshDateTime();
        Iterator<Entry<Long, List<String>>> it = expiryMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Long, List<String>> entry = it.next();
            if (System.currentTimeMillis() > entry.getKey()) {
                System.out.println(entry.getKey());
                for (String uuidAndGroupId : entry.getValue()) {
                    String groupKey = GROUP_ID_KEY_STR.concat(uuidAndGroupId);
                    String[] arr = (String[]) super.get(groupKey);
                    removeData(USERNAME_KEY_STR, arr[0], arr[1]);
                    removeData(USERPASSWORD_KEY_STR, arr[0], arr[2]);
                    removeData(USERIP_KEY_STR, arr[0], arr[3]);
                    removeData(EMAIL_KEY_STR, arr[0], arr[4]);
                    super.remove(groupKey);
                }
                it.remove();
            } else {
                break;
            }
        }
        log.info("设置失效执行时间："+(System.currentTimeMillis()-start));
    }

    private String generateKey(final String templateStr, String phraseId, String mark) {
        return phraseId.concat(templateStr).concat(mark);
    }

    private void putData(final String templateStr, String phraseId, String mark, String uuidAndGroupId) {
        if (StringUtils.isEmpty(mark)) {
            return;
        }
        super.put(generateKey(templateStr, phraseId, mark), uuidAndGroupId);
    }

    private void removeData(final String templateStr, String phraseId, String mark){
        if (StringUtils.isEmpty(mark)) {
            return;
        }
        super.remove(generateKey(templateStr, phraseId, mark));
    }

    private String getData(final String templateStr, String phraseId, String mark) {
        if (StringUtils.isEmpty(mark)) {
            return null;
        }
        return (String) super.get(generateKey(templateStr, phraseId, mark));
    }

    public Map<Long, List<String>> getExpiryMap() {
        return expiryMap;
    }
}
