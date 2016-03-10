import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserSubmission {

    /*
        Submission Sample:
            request = {
                *** "submission_id": need to figure out
                “username”: “sampleUsername”,
                “time”: current_unix_time,
                “problem”: “sampleProblem”,
                “language”: “cpp11”,
                “code”: base64(actual_code)
            };
     */

    //TODO: Figure out submission_id mechanism

    //Submission Information
    public String username;
    public long submissionTime;
    public String problem;
    public String language;
    public String sourceCode;

    //Constructor
    public UserSubmission(String jsonData) throws ParseException{
        JSONObject sub = (JSONObject) new JSONParser().parse(jsonData);
        this.username = (String) sub.get("username");
        this.submissionTime = Long.parseLong((String) sub.get("time"));
        this.problem = (String) sub.get("problem");
        this.language = (String) sub.get("language");
        this.sourceCode = decodeBase64((String) sub.get("code"));
    }

    //Private Methods
    private String decodeBase64(String base64){
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
    }

    private String encodeBase64(String text){
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(text));
    }

    //Public Methods
    public String toJSON(){
        JSONObject sub = new JSONObject();
        sub.put("username", this.username);
        sub.put("time", this.submissionTime);
        sub.put("problem", this.problem);
        sub.put("language", this.language);
        sub.put("code", encodeBase64(this.sourceCode));

        return sub.toJSONString();
    }

}
