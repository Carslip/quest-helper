/*
 * Copyright (c) 2021, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.achievementdiaries.karamja;

import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.questhelpers.ComplexStateQuestHelper;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.ZoneRequirement;
import com.questhelper.requirements.conditional.Conditions;
import com.questhelper.requirements.player.SkillRequirement;
import com.questhelper.requirements.util.LogicType;
import com.questhelper.requirements.var.VarplayerRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.DetailedQuestStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import com.questhelper.requirements.item.ItemRequirement;
import com.questhelper.QuestDescriptor;
import com.questhelper.panel.PanelDetails;
import com.questhelper.steps.QuestStep;

@QuestDescriptor(
	quest = QuestHelperQuest.KARAMJA_ELITE
)
public class KaramjaElite extends ComplexStateQuestHelper
{
	// Items required
	ItemRequirement natureTiaraOrAbyss, pureEssence, fireCapeOrInfernal, palmTreeSapling, antidotePlusPlus,
		zulrahScales, calquatSapling;

	ItemRequirement rake, spade;

	Requirement notCraftedRunes, notEquippedCape, notCheckedPalm, notCheckedCalquat, notMadePotion;

	Requirement farming72, herblore87, runecraft91;

	Requirement inNatureAltar;

	Zone natureAltar;

	QuestStep enterNatureAltar, craftRunes, equipCape, checkPalm, checkCalquat, makePotion, claimReward;

	@Override
	public QuestStep loadStep()
	{
		setupRequirements();
		setupSteps();

		ConditionalStep doElite = new ConditionalStep(this, claimReward);
		doElite.addStep(notEquippedCape, equipCape);
		doElite.addStep(notMadePotion, makePotion);
		doElite.addStep(new Conditions(notCraftedRunes, inNatureAltar), enterNatureAltar);
		doElite.addStep(notCraftedRunes, enterNatureAltar);
		doElite.addStep(notCheckedCalquat, checkCalquat);
		doElite.addStep(notCheckedPalm, checkPalm);

		return enterNatureAltar;
	}

	public void setupRequirements()
	{
		notCraftedRunes = new VarplayerRequirement(1200, false, 1);
		notEquippedCape = new VarplayerRequirement(1200, false, 2);
		notCheckedPalm = new VarplayerRequirement(1200, false, 3);
		notMadePotion = new VarplayerRequirement(1200, false, 4);
		notCheckedCalquat = new VarplayerRequirement(1200, false, 5);

		natureTiaraOrAbyss = new ItemRequirement("Nature tiara, or access to nature altar through the Abyss",
			ItemID.NATURE_TIARA).showConditioned(notCraftedRunes);
		pureEssence = new ItemRequirement("Pure essence", ItemID.PURE_ESSENCE).showConditioned(notCraftedRunes);
		fireCapeOrInfernal = new ItemRequirement("Fire cape or infernal cape", ItemID.FIRE_CAPE).showConditioned(notEquippedCape);
		fireCapeOrInfernal.addAlternates(ItemID.INFERNAL_CAPE);
		palmTreeSapling = new ItemRequirement("Palm tree sapling", ItemID.PALM_SAPLING).showConditioned(notCheckedPalm);
		antidotePlusPlus = new ItemRequirement("Antidote++", ItemID.ANTIDOTE4_5952).showConditioned(notMadePotion);
		antidotePlusPlus.addAlternates(ItemID.ANTIDOTE3_5954, ItemID.ANTIDOTE2_5956, ItemID.ANTIDOTE1_5958);
		zulrahScales = new ItemRequirement("Zulrah scales", ItemID.ZULRAHS_SCALES).showConditioned(notMadePotion);
		calquatSapling = new ItemRequirement("Calquat sapling", ItemID.CALQUAT_SAPLING).showConditioned(notCheckedCalquat);
		rake = new ItemRequirement("Rake", ItemID.RAKE).showConditioned(new Conditions(LogicType.OR, notCheckedCalquat,
			notCheckedPalm));
		spade = new ItemRequirement("Spade", ItemID.SPADE).showConditioned(new Conditions(LogicType.OR, notCheckedCalquat,
			notCheckedPalm));


		farming72 = new SkillRequirement(Skill.FARMING, 72, true);
		herblore87 = new SkillRequirement(Skill.HERBLORE, 87, true);
		runecraft91 = new SkillRequirement(Skill.RUNECRAFT, 91, true);

		natureAltar = new Zone(new WorldPoint(2374, 4809, 0), new WorldPoint(2421, 4859, 0));
		inNatureAltar = new ZoneRequirement(natureAltar);
	}

	public void setupSteps()
	{
		enterNatureAltar = new ObjectStep(this, NullObjectID.NULL_34821, new WorldPoint(2869, 3019, 0),
			"Enter the nature altar, either from the ruin or through the Abyss.", natureTiaraOrAbyss,
			pureEssence.quantity(28));
		craftRunes = new ObjectStep(this, ObjectID.ALTAR_34768, new WorldPoint(2400, 4841, 0),
			"Craft a full inventory of nature runes.", pureEssence.quantity(28));
		equipCape = new DetailedQuestStep(this, "Equip a fire or infernal cape.", fireCapeOrInfernal.equipped());
		checkPalm = new ObjectStep(this, NullObjectID.NULL_7964, new WorldPoint(2765, 3213, 0),
			"Grow and check the health of a palm tree in the Brimhaven patch.", palmTreeSapling, rake, spade);
		checkCalquat = new ObjectStep(this, NullObjectID.NULL_7807, new WorldPoint(2796, 3101, 0),
			"Grow and check the health of a Calquat in Tai Bwo Wannai.", calquatSapling, rake, spade);
		makePotion = new DetailedQuestStep(this, new WorldPoint(2734, 3224, 0), "Make an antivenom potion whilst " +
			"standing in the horse shoe mine.", antidotePlusPlus.highlighted(), zulrahScales.quantity(20).highlighted());

		claimReward = new NpcStep(this, NpcID.PIRATE_JACKIE_THE_FRUIT, new WorldPoint(2810, 3192, 0),
			"Talk to Pirate Jackie the Fruit in Brimhaven to claim your reward!");
		claimReward.addDialogStep("I have a question about my Achievement Diary.");
	}

	@Override
	public List<ItemRequirement> getItemRequirements()
	{
		return Arrays.asList(natureTiaraOrAbyss, pureEssence, fireCapeOrInfernal, palmTreeSapling, antidotePlusPlus,
			zulrahScales, calquatSapling, rake, spade);
	}

	@Override
	public List<Requirement> getGeneralRequirements()
	{
		List<Requirement> reqs = new ArrayList<>();

		reqs.add(new SkillRequirement(Skill.FARMING, 72, true));
		reqs.add(new SkillRequirement(Skill.HERBLORE, 87, true));
		reqs.add(new SkillRequirement(Skill.RUNECRAFT, 91, true));

		return reqs;
	}

	@Override
	public List<PanelDetails> getPanels()
	{
		List<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Elite Diary", Arrays.asList(equipCape, makePotion, enterNatureAltar,
			craftRunes, checkCalquat, checkPalm, claimReward), natureTiaraOrAbyss, pureEssence, fireCapeOrInfernal,
			palmTreeSapling, antidotePlusPlus, zulrahScales, calquatSapling));

		return allSteps;
	}
}
