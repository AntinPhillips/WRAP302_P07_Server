import java.util.Random;

/**
 * Created by s215055772 on 2017/10/11.
 */
public class Die {
    public int type; //green, yellow or red : 0,1,2
    public int value; //brains, runner, shotgun: 0,1,2

    public Die(int type)
    {
        this.type = type;
    }

    public int roll()
    {
        Random rand = new Random();
        int num = rand.nextInt(6);

        switch (type)
        {
            case 0:
            {
                if (num < 3)
                    value = 0;
                else if (num < 4)
                    value = 2;
                else
                    value = 1;

                break;
            }
            case 1:
            {
                if (num < 2)
                    value = 0;
                else if (num < 4)
                    value = 2;
                else
                    value = 1;

                break;
            }
            case 2:
            {
                if (num < 1)
                    value = 0;
                else if (num < 4)
                    value = 2;
                else
                    value = 1;

                break;
            }
        }

        return value;
    }
}
