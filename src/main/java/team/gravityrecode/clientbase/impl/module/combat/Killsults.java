package team.gravityrecode.clientbase.impl.module.combat;

import net.minecraft.network.play.server.S02PacketChat;
import net.optifine.util.MathUtils;
import org.apache.commons.lang3.RandomUtils;
import team.gravityrecode.clientbase.api.eventBus.EventHandler;
import team.gravityrecode.clientbase.api.moduleBase.Module;
import team.gravityrecode.clientbase.api.moduleBase.ModuleInfo;
import team.gravityrecode.clientbase.impl.event.networking.PacketEvent;
import team.gravityrecode.clientbase.impl.util.client.TimerUtil;

@ModuleInfo(moduleName = "Killsults", moduleCategory = Module.ModuleCategory.COMBAT)
public class Killsults extends Module {

    TimerUtil timeUtility = new TimerUtil();

    @EventHandler
    public void onPacket(PacketEvent eventPacket) {
        if ((mc.thePlayer != null) && (mc.thePlayer.ticksExisted >= 0)
                && ((eventPacket.getPacket() instanceof S02PacketChat))) {
            String look = "killed by " + mc.thePlayer.getName();
            String look7 = "void by " + mc.thePlayer.getName();
            String look2 = "slain by " + mc.thePlayer.getName();
            String look3 = "void while escaping " + mc.thePlayer.getName();
            String look4 = "was killed with magic while fighting " + mc.thePlayer.getName();
            String look5 = "couldn't fly while escaping " + mc.thePlayer.getName();
            String look6 = "fell to their death while escaping " + mc.thePlayer.getName();
            String look8 = "thrown off a cliff " + mc.thePlayer.getName();
            String look9 = "backstabbed by" + mc.thePlayer.getName();
            String look10 = "hit the ground too hard thanks" + mc.thePlayer.getName();
            String look11 = "slain by [";
            String name = mc.thePlayer.getName();
            S02PacketChat cp = eventPacket.getPacket();
            String cp21 = cp.getChatComponent().getUnformattedText();
            if (((cp21.startsWith(mc.thePlayer.getName() + "(")) && (cp21.contains("asesino ha")))
                    || (cp21.contains(look)) || (cp21.contains(look2)) || (cp21.contains(look3))
                    || (cp21.contains(look4)) || (cp21.contains(look5)) || (cp21.contains(look6))
                    || (cp21.contains(look7)) || (cp21.contains(look8)) || (cp21.contains(look9)) || (cp21.contains(look10))|| ((cp21.contains(look10)) && cp21.contains(name)) || (cp21.contains("You have been rewarded $50 and 2 point(s)!"))
                    && timeUtility.hasElapsed(50)) {
                mc.thePlayer.sendChatMessage("" + randomMessage() + " [" + RandomUtils.nextLong(4444L, 100000000L) + "]");
                timeUtility.reset();
            }
        }
    }

    private String randomMessage() {
        MathUtils mathUtility = new MathUtils();
        String[] randomMessages = {"Mirrors cant talk, lucky for you they cant laugh either",
                "Hey you have something on your chin, no, the third one down",
                "If i wanted to die, i would jump from your ego to your iq",
                "Just because your head is shaped like a lightbulb doesnt mean you have good ideas",
                "If tiktok was an airport for idiots, you'd be booked for a lifetime",
                "Your brain is so smooth, not even a 3090 ti can simulate the reflectiveness",
                "Your brain is so smooth, not even king neptune can compete with the reflections",
                "Some kids get dropped at birth, you got sat on by your 600 pound mom",
                "I see why your dad left and didn't come back.",
                "You're the reason the gene pool needs a lifeguard",
                "If I had a face like yours, i'd sue my parents",
                "You must have been born on the highway, because that's where most accidents happen",
                "If clowns really make people laugh, i bet you get hired for parties a lot",
                "I'd agree with you, but then we'd both be wrong",
                "You're so fat, the floor screams when you walk",
                "If I owed a dollar whenever you said something smart, i would be millions in debt",
                "If I had a dollar for everything smart you said, I'd be broke",
                "You're as useless as the 'ueue' in 'queue'",
                "Its impossible to underestimate you",
                "Im not a nerd, im just smarter than you",
                "Your face is just fine but we’ll have to put a bag over that personality",
                "I thought of you today. It reminded me to take out the trash",
                "Keep rolling your eyes, you might eventually find a brain",
                "Our kid must have gotten his brain from you! I still have mine",
                "I’d rather treat my baby’s diaper rash than have lunch with you",
                "You are like a cloud. When you disappear it’s a beautiful day",
                "You just might be why the middle finger was invented in the first place",
                "Wish I had a flip phone so I could slam it shut on this conversation",
                "You cute. Like my dog. He also always chases his tail for entertainment",
                "Well, the jerk store called and they’re running out of you",
                "Beauty is only skin deep, but ugly goes clean to the bone",
                "Somewhere out there is a tree tirelessly producing oxygen for you. You owe it an apology",
                "Your mom gave birth to you at a movie theater",
                "Since you know it all, you should know when to shut up",
                "My middle finger salutes you",
                "I hope one day you choke on all the shit you talk",
                "Life is full of disappointments and I just added you to the list",
                "If you were a vegetable, you would be a cabbitch",
                "I'd call you a dick, but you're not real enough",
                "Blind kids cry when they look at you",
                "I would make a joke about your life, but life beat me to it",
                "Your mother should have swallowed you",
                "You make me wish I had more middle fingers",
                "The trash gets picked up tomorrow, be ready",
                "I don't know whats worse, your high ego or your low iq",
                "I don't know whats worse, your iq or your hairline",
                "Pictures of you come up when i google 'idiot'",
                "You're about as useful as a white crayon with white paper",
                "You'll never be the man that your mother is",
                "The last time i saw something like you, I flushed it",
                "Two wrongs don't make a right, take your parents for example",
                "Im not stupid, I'm just following your example",
                "You're so fake, even china refused to make you",
                "If you're gonna be two-faced, at least make sure one of them isn't ugly",
                "I like your approach, now lets see your departure",
                "Im jealous of all the people who haven't met you",
                "You are the reason god created the middle finger",
                "I would prefer a battle of wits, but you appear to be unarmed",
                "In the land of the witless, you would be the king",
                "I can see you were an experiment in god's game of the sims",
                "You are physical proof of reverse evolution",
                "You are proof god has a sense of humor",
                "People clap when they see you. They clap their hands over their eyes",
                "im not saying youre retarded, im just saying your brain is a tumor",
                "you playing minecraft is as much as of accident as your birth",
                "did you lose your brain in a car accident?"};
        return randomMessages[RandomUtils.nextInt(0, 65)];
    }
}
