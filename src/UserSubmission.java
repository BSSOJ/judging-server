import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UserSubmission {

    /*
        Introduction:
            This is a Java object that contains the information of a user submission.

        Fields:
            String username         Name of the user who submitted the solution
            long submissionTime     UNIX time of when the code was submitted
            String problem          Problem ID for the submission
            String language         The language for the submission
            String sourceCode       Clear text format of source code

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

    /*
        Constructor:
            Takes the JSON data and parses the fields to fill the fields of the
            object. It also decodes the Base64 code into actual readable / compilable
            source code, ready to be judged by the docker instance.
     */
    public UserSubmission(String jsonData) throws ParseException{
        JSONObject sub = (JSONObject) new JSONParser().parse(jsonData);
        this.username = (String) sub.get("username");
        this.submissionTime = Long.parseLong((String) sub.get("time"));
        this.problem = (String) sub.get("problem");
        this.language = (String) sub.get("language");
        this.sourceCode = decodeBase64((String) sub.get("code"));
    }

    /*
        Private methods for Base64 manipulation
     */
    private String decodeBase64(String base64){
        return StringUtils.newStringUtf8(Base64.decodeBase64(base64));
    }

    private String encodeBase64(String text){
        return Base64.encodeBase64String(StringUtils.getBytesUtf8(text));
    }

    /*
        Creates a JSON string about the current submission. The source code will
        be converted back to Base64
     */
    public String toJSONString(){
        JSONObject sub = new JSONObject();
        sub.put("username", this.username);
        sub.put("time", this.submissionTime);
        sub.put("problem", this.problem);
        sub.put("language", this.language);
        sub.put("code", encodeBase64(this.sourceCode));

        return sub.toJSONString();
    }

}
