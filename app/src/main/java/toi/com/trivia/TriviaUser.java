package toi.com.trivia;

/**
 * Created by Akanksha on 25/05/17.
 */

public class TriviaUser {
    private String email;
    private String name;
    private String uuid;
    private String imageUrl;
    private String socialImageUrl;

    public TriviaUser() {

    }

    public String getEmail() {
        return this.email;
    }

    public String getName() {
        return this.name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public String getSocialImageUrl() {
        return this.socialImageUrl;
    }


    public static class Builder {
        //required
        private final String email;
        private final String name;
        private final String uuid;
        private final String imageUrl;
        private final String socialImageUrl;

        //optional
        private boolean cheese = false;


        public Builder(String email, String name, String uuid, String imageUrl, String socialImageUrl) {
            this.email = email;
            this.name = name;
            this.uuid = uuid;
            this.imageUrl = imageUrl;
            this.socialImageUrl = socialImageUrl;
        }


        //example
       /* public Builder cheese(boolean value) {
            cheese = value;
            return this;
        }*/


        public TriviaUser build() {
            return new TriviaUser(this);
        }
    }

    private TriviaUser(Builder builder) {
        this.email = builder.email;
        this.name = builder.name;
        this.uuid = builder.uuid;
        this.imageUrl = builder.imageUrl;
        this.socialImageUrl = builder.socialImageUrl;
    }
}
